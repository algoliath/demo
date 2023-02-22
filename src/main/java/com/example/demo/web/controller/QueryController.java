package com.example.demo.web.controller;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.data.vo.SQLBlockForm;
import com.example.demo.domain.database.form.*;
import com.example.demo.domain.database.model.SQLBlock;
import com.example.demo.domain.database.model.SQLBlockData;
import com.example.demo.domain.database.model.SQLBlockType;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import com.example.demo.domain.source.datasource.SpreadSheetSource;
import com.example.demo.domain.template.form.QueryTemplateForm;
import com.example.demo.domain.template.model.Query;
import com.example.demo.domain.template.model.Template;
import com.example.demo.domain.template.service.TemplateService;
import com.example.demo.util.Source;
import com.example.demo.util.template.TemplateFormProvider;
import com.example.demo.util.validation.QueryUtils;
import com.example.demo.web.session.SessionConst;
import com.github.pagehelper.PageHelper;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.model.Sheet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.demo.domain.source.datasource.SpreadSheetSource.*;
import static com.example.demo.util.controller.ControllerUtils.*;
import static com.example.demo.util.database.DBConnectionUtils.getConnection;

@Slf4j
@Controller
@RequestMapping("/template/query")
public class QueryController {

    private final MemberRepository memberRepository;
    private final TemplateFormProvider templateFormProvider;
    private final TemplateService templateService;
    private static final String VIEW_URL = "template/query/add::#add_template";

    @Autowired
    public QueryController(MemberRepository memberRepository, TemplateFormProvider templateFormProvider, TemplateService templateService) {
        this.memberRepository = memberRepository;
        this.templateFormProvider = templateFormProvider;
        this.templateService = templateService;
    }

    @GetMapping(value="/delete_template/{memberId}")
    public String deleteTemplate(@ModelAttribute SQLBlockForm sqlBlockForm, @PathVariable String memberId, Model model){
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        SQLBlockData sqlBlockData = queryTemplateForm.getSQLForm().getSQLBlock(sqlBlockForm.getSqlBlockOrder()).getSQLBlockData(sqlBlockForm.getSqlDataOrder());
        String queryName = sqlBlockForm.getTemplateName();
        sqlBlockData.deleteQuery(queryName);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return "template/query/add";
    }

    @PostMapping(value="/add_template/{memberId}")
    public String addTemplate(@ModelAttribute SQLBlockForm sqlBlockForm, @PathVariable String memberId, Model model) {
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        SQLForm sqlForm = queryTemplateForm.getSQLForm();
        String templateName = sqlBlockForm.getTemplateName();
        List<Template> templatesByName = templateService.findTemplatesByName(templateName);

        if(templatesByName.isEmpty()){
            throw new IllegalArgumentException("찾고자 하는 템플릿 이름이 존재하지 않습니다");
        }

        PageHelper.startPage(1, 1, false);
        List<Template> templates = templateService.findTemplatesByName(templateName);
        Query template = (Query) templates.get(0);
        List<Column> columns = templateService.findColumnsByTemplateId(template.getId());
        template.setColumns(columns);

        SQLBlockData target = sqlForm.getSQLBlock(sqlBlockForm.getSqlBlockOrder()).getSQLBlockData(sqlBlockForm.getSqlDataOrder());
        target.getQueries().add(template);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return "template/query/add";
    }

    @PostMapping("/search_column/{memberId}")
    public String searchColumn(@ModelAttribute SQLBlockForm sqlBlockForm, BindingResult bindingResult,
                               @PathVariable String memberId, Model model){

        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        String queryName = sqlBlockForm.getTemplateName().trim();

        log.info("queryName={}", queryName);
        SQLBlockData target = queryTemplateForm.getSQLBlock(sqlBlockForm.getSqlBlockOrder()).getSQLBlockData(sqlBlockForm.getSqlDataOrder());
        Query query = target.getQueries().stream().filter(template -> template.getName().trim().equals(queryName)).findAny().get();
        List<Template> templates = templateService.findTemplatesByName(queryName);

        // 서브 쿼리 타입
        if(templates.isEmpty()){
            query.setAlias("SUB_QUERY" + "_" + target.getSqlBlockOrder() + "_" + target.getSqlDataOrder());
            query.setOperator(sqlBlockForm.getOperator());
        }

        Query targetQuery = (Query) templates.get(0);
        List<String> parsedColumns = targetQuery.getColumns().stream().map(column -> column.getColumnName().getValidName()).collect(Collectors.toList());
        model.addAttribute("columns", parsedColumns);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return VIEW_URL;
    }

    @PostMapping("/add_columns/{memberId}")
    public String addColumn(@ModelAttribute SQLBlockForm sqlBlockForm, BindingResult bindingResult,
                            @PathVariable String memberId, Model model){

        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        SQLBlock sqlBlock = queryTemplateForm.getSQLBlock(sqlBlockForm.getSqlBlockOrder());
        SQLBlockData target = sqlBlock.getDataHolder().get(sqlBlockForm.getSqlDataOrder());
        String queryName = sqlBlockForm.getTemplateName();
        Optional<Query> queryOptional = target.getQuery(queryName);

        if(queryOptional.isEmpty()){
           throw new IllegalArgumentException("찾고자 하는 템플릿 이름이 존재하지 않습니다");
        }

        Query query = queryOptional.get();
        switch (sqlBlock.getSQLBlockType()){
            case WHERE, HAVING -> {
                query.setOperator(sqlBlockForm.getOperator());
                query.setOperand(sqlBlockForm.getOperand());
            }
        }

        log.info("target={}", target);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return VIEW_URL;
    }

    @GetMapping("/add_query_data/{memberId}")
    public String addData(@Validated @ModelAttribute SQLBlockForm sqlBlockForm, BindingResult bindingResult,
                          @PathVariable String memberId, Model model){
        Integer sqlBlockOrder = sqlBlockForm.getSqlBlockOrder();
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);

        if(bindingResult.hasErrors()){
            return VIEW_URL;
        }

        SQLBlock sqlBlock = queryTemplateForm.getSQLForm().getSqlBlockList().get(sqlBlockOrder);
        SQLBlockData addSQLBlockData = new SQLBlockData(sqlBlock.getSQLBlockType(), sqlBlockForm.getSqlBlockOrder(), sqlBlockForm.getSqlDataOrder()+1);
        sqlBlock.addData(addSQLBlockData);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return VIEW_URL;
    }

    @GetMapping("/delete_query_data/{memberId}")
    public String removeData(@Validated @ModelAttribute SQLBlockForm sqlBlockForm, BindingResult bindingResult,
                             @PathVariable String memberId, Model model){
        if(bindingResult.hasErrors()){
            return VIEW_URL;
        }
        int sqlBlockOrder = sqlBlockForm.getSqlBlockOrder();
        int sqlDataOrder = sqlBlockForm.getSqlDataOrder();
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        List<SQLBlock> sqlBlockList = queryTemplateForm.getSQLForm().getSqlBlockList();
        SQLBlock sqlBlock = sqlBlockList.get(sqlBlockOrder);
        sqlBlock.removeData(sqlDataOrder);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return VIEW_URL;
    }

    @GetMapping("/add_block/{memberId}")
    public String addBlock(@ModelAttribute SQLBlockForm sqlBlockForm, BindingResult bindingResult,
                           @PathVariable String memberId, Model model){
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        List<SQLBlock> sqlBlockList = queryTemplateForm.getSQLForm().getSqlBlockList();
        int sqlBlockOrder = sqlBlockForm.getSqlBlockOrder();
        sqlBlockList.add(sqlBlockOrder, new SQLBlock(sqlBlockOrder+1));
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return VIEW_URL;
    }

    @PostMapping("/add_sub_query/{memberId}")
    public String addSubQuery(@ModelAttribute SQLBlockForm sqlBlockForm, BindingResult bindingResult,
                              @PathVariable String memberId, Model model){

        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        List<SQLBlock> sqlBlockList = queryTemplateForm.getSQLForm().getSqlBlockList();
        sendQueryForwardModelAttributes(model, queryTemplateForm);

        List<SQLBlock> targetSQLBlockList = sqlBlockForm.getIndices()
                .stream()
                .map(sqlBlockList::get)
                .collect(Collectors.toList());

        if(targetSQLBlockList.isEmpty()){
            return VIEW_URL;
        }

        for(SQLBlock targetSQLBlock: targetSQLBlockList){
            sqlBlockList.remove(targetSQLBlock);
        }

        Query subQuery = new Query();
        subQuery.setName(QueryUtils.convertSQLBlocks(targetSQLBlockList));
        log.info("query={}", subQuery.getName());

        for(SQLBlock sqlBlock: targetSQLBlockList){
            if(sqlBlock.getSQLBlockType() == SQLBlockType.SELECT){
                for(SQLBlockData sqlBlockData: sqlBlock.getDataHolder()){
                    List<Column> columns = new ArrayList<>();
                    Query query = sqlBlockData.getQuery(0);
                    for(Column column: query.getColumns()){
                        columns.add(column);
                    }
                    query.setColumns(columns);
                }
            }
        }

        int startIndex = sqlBlockForm.getIndices().get(0);
        SQLBlock addSQLBlock = new SQLBlock(SQLBlockType.SELECT, startIndex);
        SQLBlockData addSQLBlockData = new SQLBlockData(SQLBlockType.SELECT, startIndex, 0);
        subQuery.setIsSubQuery(true);
        addSQLBlockData.getQueries().add(subQuery);
        addSQLBlock.addData(addSQLBlockData);
        sqlBlockList.add(startIndex, addSQLBlock);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return VIEW_URL;
    }

    @GetMapping("/delete_block/{memberId}")
    public String removeBlock(@Validated @ModelAttribute SQLBlockForm sqlBlockForm, BindingResult bindingResult,
                              @PathVariable String memberId, Model model){
        if(bindingResult.hasErrors()){
            return VIEW_URL;
        }
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        List<SQLBlock> sqlBlockList = queryTemplateForm.getSQLForm().getSqlBlockList();
        int sqlBlockOrder = sqlBlockForm.getSqlBlockOrder();
        sqlBlockList.remove(sqlBlockOrder);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return VIEW_URL;
    }

    @PostMapping("/edit_block/{memberId}")
    public String editBlock(@Validated @ModelAttribute SQLBlockForm sqlBlockForm, BindingResult bindingResult,
                            @PathVariable String memberId, Model model){
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);

        if(bindingResult.hasErrors()){
            log.info("bindingResult={}", bindingResult);
            return VIEW_URL;
        }

        List<SQLBlock> sqlBlockList = queryTemplateForm.getSQLForm().getSqlBlockList();
        SQLBlock targetSQLBlock = sqlBlockList.get(sqlBlockForm.getSqlBlockOrder());
        targetSQLBlock.setSQLBlockType(sqlBlockForm.getSQLBlockType());
        for (SQLBlockData sqlBlockData: targetSQLBlock.getDataHolder()) {
             sqlBlockForm.setSQLBlockType(sqlBlockData.getSQLBlockType());
        }

        sendQueryPostModelAttributes(model, queryTemplateForm);
        return VIEW_URL;
    }

    @PostMapping("/execute_query/{memberId}")
    public String executeQuery(@ModelAttribute SQLBlockForm sqlBlockForm, BindingResult bindingResult,
                               @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member,
                               @PathVariable String memberId, Model model){

        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        SQLForm sqlForm = queryTemplateForm.getSQLForm();
        PreparedStatement preparedStatement;
        Connection connection;

        sendQueryForwardModelAttributes(model, queryTemplateForm);
        List<SQLBlock> sqlBlockList = queryTemplateForm.getSQLForm().getSqlBlockList();

        try{
            StringBuilder queryBuilder = new StringBuilder();
            for(SQLBlock sqlBlock: sqlBlockList){
                queryBuilder.append(sqlBlock.getSqlQuery());
                queryBuilder.append(" ");
            }

            String query = queryBuilder.toString();
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<List<Object>> resultList = new ArrayList<>();

            int rowCount = 0;
            int columnCount = resultSet.getMetaData().getColumnCount();

            while(resultSet.next()){
                sqlForm.getSqlBlockList().stream().iterator().forEachRemaining(sqlBlock -> sqlBlock.setSqlQuery(QueryUtils.convertSQLBlock(sqlBlock)));                log.info("result={}", resultSet);
                List<Object> row = new ArrayList<>();
                resultList.add(row);
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                     row.add(resultSet.getObject(columnIndex));
                }
                rowCount++;
            }

            log.info("result={}", resultList);
            Member loginMember = memberRepository.findById(member.getId()).get();
            SpreadSheetSource spreadSheetSource = new SpreadSheetSource(loginMember.getFileId());

            Source<String> paramSource = new Source<>();
            paramSource.add(FILE_NAME, "QUERY_"+loginMember.getLoginId());
            List<File> files = spreadSheetSource.getFiles(paramSource);
            File targetFile = (!files.isEmpty())? files.get(0): spreadSheetSource.createFile(paramSource);

            Sheet sheet = spreadSheetSource.get(targetFile);
            String sheetRange = sheet.getProperties().getTitle() + "!A1:" + (char)('A'+columnCount) + "" + rowCount;
            paramSource.add(FILE_RANGE, sheetRange);
            spreadSheetSource.publish(paramSource);

            spreadSheetSource.clear(paramSource, sheetRange);
            spreadSheetSource.write(paramSource, resultList);
            queryTemplateForm.setSourceId(targetFile.getId());
            queryTemplateForm.getSQLForm().setSpreadSheetTable(new SpreadSheetTable(sheetRange, resultList));
        } catch (SQLException e) {
            sqlForm.setSqlError(e.getSQLState());
        }

        sendQueryPostModelAttributes(model, queryTemplateForm);
        return "/template/query/add";
    }

}
