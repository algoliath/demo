package com.example.demo.web.controller;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.property.name.ColumnName;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.data.vo.SearchForm;
import com.example.demo.domain.database.form.*;
import com.example.demo.domain.database.model.SQLBlock;
import com.example.demo.domain.database.model.SQLBlockData;
import com.example.demo.domain.database.model.SQLBlockType;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import com.example.demo.domain.source.datasource.SpreadSheetSource;
import com.example.demo.domain.template.form.QueryTemplateForm;
import com.example.demo.domain.template.form.TemplateForm;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.model.Template;
import com.example.demo.domain.template.service.TemplateService;
import com.example.demo.domain.template.type.TemplateType;
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private static final String ASYNC_VIEW_URL = "template/query/add::#add_template";

    @Autowired
    public QueryController(MemberRepository memberRepository, TemplateFormProvider templateFormProvider, TemplateService templateService) {
        this.memberRepository = memberRepository;
        this.templateFormProvider = templateFormProvider;
        this.templateService = templateService;
    }

    @GetMapping(value="/search_entity/{memberId}", consumes="application/x-www-form-urlencoded;charset=UTF-8;")
    public String searchTemplate(@ModelAttribute SearchForm searchForm, @PathVariable String memberId, Model model) {
        TemplateForm templateForm = templateFormProvider.getTemplateForm(memberId);
        String templateName = searchForm.getName();
        String fragment = searchForm.getFragment();
        templateForm.setName(templateName);
        templateForm.setType(TemplateType.ENTITY.name());
        PageHelper.startPage(1, 1, false);

        List<Template> templates = templateService.findTemplatesByName(templateName);
        templates.remove(new Template(templateForm));
        model.addAttribute("templates", templates);
        log.info("templates={}", templates);
        log.info("fragment={}", fragment);
        return "template/query/add::#" + fragment;
    }

    @GetMapping(value="/delete_template/{memberId}/{templateName}")
    public String deleteTemplate(@PathVariable String memberId, @PathVariable String templateName, Model model){
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);

        List<Entity> joinTemplates = queryTemplateForm.getJoinTemplates();
        Entity targetEntity = queryTemplateForm.getJoinTemplates().stream().filter(template -> template.getName().equals(templateName)).findAny().get();
        joinTemplates.remove(targetEntity);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return "template/query/add";
    }

    @PostMapping(value="/add_entity/{memberId}")
    public String addTemplate(@ModelAttribute SearchForm searchForm, @PathVariable String memberId, Model model) {
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        TemplateForm templateForm = templateFormProvider.getTemplateForm(memberId);
        String templateName = searchForm.getName();

        templateForm.setName(templateName);
        templateForm.setType(TemplateType.QUERY.name());
        PageHelper.startPage(1, 1, false);

        List<Template> templates = templateService.findTemplatesByName(templateName);
        Entity template = (Entity) templates.get(0);
        List<Column> columns = templateService.findColumnsByTemplateId(template.getId());
        template.setColumns(columns);
        queryTemplateForm.getJoinTemplates().add(template);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return "template/query/add";
    }

    @PostMapping("/search_column/{memberId}")
    public String searchColumn(@ModelAttribute SQLBlockData sqlBlockData, BindingResult bindingResult,
                               @PathVariable String memberId, Model model){

        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);

        String templateName = sqlBlockData.getTemplateName().trim();
        log.info("templateName={}", templateName);
        log.info("joinTemplates={}", queryTemplateForm.getJoinTemplates().stream().map(Template::getName).collect(Collectors.toList()));
        Entity entity = queryTemplateForm.getJoinTemplates().stream().filter(template -> template.getName().trim().equals(templateName)).findAny().get();
        SQLBlockData target = queryTemplateForm.getSQLBlock(sqlBlockData.getSqlBlockOrder()).getDataHolder().get(sqlBlockData.getSqlDataOrder());
        target.setTemplateName(templateName);

        List<Template> templates = templateService.findTemplatesByName(templateName);
        target.setTemplateName(templateName);

        // 서브 쿼리 타입
        if(templates.isEmpty()){
            target.setTemplateAlias("SUB_QUERY" + "_" + target.getSqlBlockOrder() + "_" + target.getSqlDataOrder());
            target.setOperator(sqlBlockData.getOperator());
        }

        List<String> parsedColumns = entity.getColumns().stream().map(column -> column.getColumnName().getValidName()).collect(Collectors.toList());
        target.setColumns(parsedColumns);
        model.addAttribute("columns", parsedColumns);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return ASYNC_VIEW_URL;
    }

    @PostMapping("/add_columns/{memberId}")
    public String addColumn(@ModelAttribute SQLBlockData sqlBlockData, BindingResult bindingResult,
                            @PathVariable String memberId, Model model){

        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        SQLBlock sqlBlock = queryTemplateForm.getSQLBlock(sqlBlockData.getSqlBlockOrder());
        SQLBlockData target = sqlBlock.getDataHolder().get(sqlBlockData.getSqlDataOrder());
        target.setTargetColumns(sqlBlockData.getTargetColumns());
        target.setTemplateName(sqlBlockData.getTemplateName());

        switch (sqlBlock.getSQLBlockType()){
            case WHERE, HAVING -> {
                target.setOperator(sqlBlockData.getOperator());
                target.setOperand(sqlBlockData.getOperand());
            }
        }

        log.info("target={}", target);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return ASYNC_VIEW_URL;
    }

    @GetMapping("/add_query_data/{memberId}")
    public String addData(@Validated @ModelAttribute SQLBlockData sqlBlockData, BindingResult bindingResult,
                          @PathVariable String memberId, Model model){
        Integer sqlBlockOrder = sqlBlockData.getSqlBlockOrder();
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);

        if(bindingResult.hasErrors()){
            return ASYNC_VIEW_URL;
        }

        SQLBlock sqlBlock = queryTemplateForm.getSQLForm().getSqlBlockList().get(sqlBlockOrder);
        SQLBlockData addSQLBlockData = new SQLBlockData(sqlBlock.getSQLBlockType(), sqlBlockData.getSqlBlockOrder(), sqlBlockData.getSqlDataOrder()+1);
        List<SQLBlockData> dataHolder = sqlBlock.getDataHolder();
        dataHolder.add(addSQLBlockData);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return ASYNC_VIEW_URL;
    }

    @GetMapping("/delete_query_data/{memberId}")
    public String removeData(@Validated @ModelAttribute SQLBlockData sqlBlockData, BindingResult bindingResult,
                             @PathVariable String memberId, Model model){
        if(bindingResult.hasErrors()){
            return ASYNC_VIEW_URL;
        }
        int sqlBlockOrder = sqlBlockData.getSqlBlockOrder();
        int sqlDataOrder = sqlBlockData.getSqlDataOrder();
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        List<SQLBlock> sqlBlockList = queryTemplateForm.getSQLForm().getSqlBlockList();
        SQLBlock sqlBlock = sqlBlockList.get(sqlBlockOrder);

        List<SQLBlockData> dataHolder = sqlBlock.getDataHolder();
        dataHolder.remove(sqlDataOrder);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return ASYNC_VIEW_URL;
    }

    @GetMapping("/add_block/{memberId}")
    public String addBlock(@ModelAttribute SQLBlock sqlBlock, BindingResult bindingResult,
                           @PathVariable String memberId, Model model){
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        List<SQLBlock> sqlBlockList = queryTemplateForm.getSQLForm().getSqlBlockList();
        int sqlBlockOrder = sqlBlock.getOrder();
        sqlBlockList.add(sqlBlockOrder, new SQLBlock(sqlBlockOrder+1));
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return ASYNC_VIEW_URL;
    }

    @PostMapping("/add_sub_query/{memberId}")
    public String addSubQuery(@ModelAttribute SQLForm sqlForm, BindingResult bindingResult,
                              @PathVariable String memberId, Model model){

        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        List<SQLBlock> sqlBlockList = queryTemplateForm.getSQLForm().getSqlBlockList();
        sendQueryForwardModelAttributes(model, queryTemplateForm);

        List<SQLBlock> targetSQLBlockList = sqlForm.getIndices()
                .stream()
                .map(sqlBlockList::get)
                .collect(Collectors.toList());

        if(targetSQLBlockList.isEmpty()){
            return ASYNC_VIEW_URL;
        }

        for(SQLBlock targetSQLBlock: targetSQLBlockList){
            sqlBlockList.remove(targetSQLBlock);
        }

        Entity entity = new Entity();
        entity.setName(QueryUtils.convertSQLBlocks(targetSQLBlockList));
        log.info("entity={}", entity.getName());

        for(SQLBlock sqlBlock: targetSQLBlockList){
            if(sqlBlock.getSQLBlockType() == SQLBlockType.SELECT){
                for(SQLBlockData sqlBlockData: sqlBlock.getDataHolder()){
                    List<Column> columns = new ArrayList<>();
                    for(String columnName: sqlBlockData.getColumns()){
                        Column column = new Column();
                        column.setColumnName(new ColumnName(columnName));
                        columns.add(column);
                    }
                    entity.setColumns(columns);
                }
            }
        }

        log.info("sqlForm={}", sqlForm);
        queryTemplateForm.getJoinTemplates().add(entity);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return ASYNC_VIEW_URL;
    }

    @GetMapping("/delete_block/{memberId}")
    public String removeBlock(@Validated @ModelAttribute SQLBlock sqlBlock, BindingResult bindingResult,
                              @PathVariable String memberId, Model model){
        if(bindingResult.hasErrors()){
            return ASYNC_VIEW_URL;
        }
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        List<SQLBlock> sqlBlockList = queryTemplateForm.getSQLForm().getSqlBlockList();
        int sqlBlockOrder = sqlBlock.getOrder();
        sqlBlockList.remove(sqlBlockOrder);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return ASYNC_VIEW_URL;
    }

    @PostMapping("/edit_block/{memberId}")
    public String editBlock(@Validated @ModelAttribute SQLBlock sqlBlock, BindingResult bindingResult,
                            @PathVariable String memberId, Model model){
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);

        if(bindingResult.hasErrors()){
            log.info("bindingResult={}", bindingResult);
            return ASYNC_VIEW_URL;
        }

        List<SQLBlock> sqlBlockList = queryTemplateForm.getSQLForm().getSqlBlockList();
        SQLBlock targetSQLBlock = sqlBlockList.get(sqlBlock.getOrder());
        targetSQLBlock.setSQLBlockType(sqlBlock.getSQLBlockType());
        for (SQLBlockData sqlBlockData: targetSQLBlock.getDataHolder()) {
             sqlBlockData.setSQLBlockType(sqlBlock.getSQLBlockType());
        }
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return ASYNC_VIEW_URL;
    }

    @PostMapping("/execute_query/{memberId}")
    public String executeQuery(@ModelAttribute SQLForm sqlForm, BindingResult bindingResult,
                               @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member,
                               @PathVariable String memberId, Model model){

        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
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
            paramSource.add(FILE_NAME, "QUERY_"+loginMember.getRandomUUID());
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
