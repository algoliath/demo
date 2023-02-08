package com.example.demo.web.template;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.property.name.ColumnName;
import com.example.demo.domain.data.vo.SearchForm;
import com.example.demo.domain.database.form.*;
import com.example.demo.domain.database.model.SQLBlock;
import com.example.demo.domain.database.model.SQLBlockData;
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
import com.example.demo.web.session.SessionConst;
import com.github.pagehelper.PageHelper;
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
        queryTemplateForm.getJoinTemplates().add(template);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return "template/query/add";
    }

    @PostMapping("/search_column/{memberId}")
    public String searchColumn(@ModelAttribute SQLBlockData sqlBlockData, BindingResult bindingResult,
                               @PathVariable String memberId, Model model){

        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);

        String templateName = sqlBlockData.getTemplateName();
        PageHelper.startPage(1, 1, false);

        List<Template> templates = templateService.findTemplatesByName(templateName);
        if(templates.size() != 1){
            throw new IllegalStateException();
        }

        Entity template = (Entity) templates.get(0);
        SQLBlockData target = queryTemplateForm.getSQLBlock(sqlBlockData.getSqlBlockOrder()).getDataHolder().get(sqlBlockData.getSqlDataOrder());
        target.setTemplateName(templateName);
        List<Column> columns = templateService.findColumnsByTemplateId(template.getId());
        List<String> parsedColumns = columns.stream().map(column -> column.getColumnName().getValidName()).collect(Collectors.toList());
        target.setColumns(parsedColumns);
        model.addAttribute("columns",parsedColumns);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return "template/query/add::#add_template";
    }

    @PostMapping("/add_columns/{memberId}")
    public String addColumn(@ModelAttribute SQLBlockData sqlBlockData, BindingResult bindingResult,
                            @PathVariable String memberId, Model model){

        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        log.info("sqlBlockData={}", sqlBlockData);
        sendQueryForwardModelAttributes(model, queryTemplateForm);

        SQLBlock sqlBlock = queryTemplateForm.getSQLBlock(sqlBlockData.getSqlBlockOrder());
        sqlBlock.setSqlBlockType(sqlBlockData.getSqlBlockType());

        SQLBlockData target = sqlBlock.getDataHolder().get(sqlBlockData.getSqlDataOrder());
        target.setTargetColumns(sqlBlockData.getTargetColumns());
        target.setTemplateName(sqlBlockData.getTemplateName());

        switch (sqlBlock.getSqlBlockType()){
            case WHERE, HAVING -> {
                target.setOperator(sqlBlockData.getOperator());
                target.setOperand(sqlBlockData.getOperand());
            }
        }

        sendQueryPostModelAttributes(model, queryTemplateForm);
        return "template/query/add::#add_template";
    }

    @GetMapping("/add_query_data/{memberId}")
    public String addData(@Validated @ModelAttribute SQLBlockData sqlBlockData, BindingResult bindingResult,
                          @PathVariable String memberId, Model model){
        Integer sqlBlockOrder = sqlBlockData.getSqlBlockOrder();
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);

        if(bindingResult.hasErrors()){
            return "/template/query/add::#add_template";
        }

        SQLBlock sqlBlock = queryTemplateForm.getSQLForm().getSqlBlockList().get(sqlBlockOrder);
        sqlBlockData.setSqlBlockType(sqlBlock.getSqlBlockType());
        sqlBlockData.setSqlDataOrder(sqlBlockData.getSqlDataOrder()+1);
        List<SQLBlockData> dataHolder = sqlBlock.getDataHolder();
        dataHolder.add(sqlBlockData);
        return "/template/query/add::#add_template";
    }

    @GetMapping("/delete_query_data/{memberId}")
    public String removeData(@Validated @ModelAttribute SQLBlockData sqlBlockData, BindingResult bindingResult,
                             @PathVariable String memberId, Model model){
        if(bindingResult.hasErrors()){
            return "/template/query/add::#add_template";
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
        return "/template/query/add::#add_template";
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
        return "/template/query/add::#add_template";
    }

    @PostMapping("/add_sub_block/{memberId}")
    public String addSubBlock(@ModelAttribute SQLBlock sqlBlock, BindingResult bindingResult,
                              @PathVariable String memberId, Model model){
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        SQLBlock targetSQLBlock = queryTemplateForm.getSQLForm().getSqlBlockList().get(sqlBlock.getOrder());
        for(SQLBlockData data: targetSQLBlock.getDataHolder()){
            Entity entity = new Entity();
            entity.setName(data.getTemplateName());
            List<Column> columns = new ArrayList<>();
            for(String columnName: data.getTargetColumns()){
                Column column = new Column();
                column.setColumnName(new ColumnName(columnName));
                columns.add(column);
            }
            entity.setColumns(columns);
            queryTemplateForm.getSubQueryTemplates().add(entity);
        }
        queryTemplateForm.getSQLForm().getSqlBlockList().remove(sqlBlock);
        return "/template/query/add::#add_template";
    }

    @GetMapping("/delete_block/{memberId}")
    public String removeBlock(@Validated @ModelAttribute SQLBlock sqlBlock, BindingResult bindingResult,
                              @PathVariable String memberId, Model model){
        if(bindingResult.hasErrors()){
            return "/template/query/add::#add_template";
        }
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        List<SQLBlock> sqlBlockList = queryTemplateForm.getSQLForm().getSqlBlockList();
        int sqlBlockOrder = sqlBlock.getOrder();
        sqlBlockList.remove(sqlBlockOrder);
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return "/template/query/add::#add_template";
    }

    @PostMapping("/edit_block/{memberId}")
    public String editBlock(@Validated @ModelAttribute SQLBlock sqlBlock, BindingResult bindingResult,
                            @PathVariable String memberId, Model model){
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        if(bindingResult.hasErrors()){
            log.info("bindingResult={}", bindingResult);
            return "/template/query/add::#add_template";
        }
        List<SQLBlock> sqlBlockList = queryTemplateForm.getSQLForm().getSqlBlockList();
        SQLBlock targetSqlBlock = sqlBlockList.get(sqlBlock.getOrder());
        targetSqlBlock.clear();
        targetSqlBlock.setSqlBlockType(sqlBlock.getSqlBlockType());
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return "/template/query/add::#add_template";
    }

    @PostMapping("/convert_block/{memberId}")
    public String convertBlock(@Validated @ModelAttribute SQLBlock sqlBlock, BindingResult bindingResult,
                               @PathVariable String memberId, Model model){
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        sendQueryForwardModelAttributes(model, queryTemplateForm);
        String partialQuery = "";

        log.info("sqlBlock={}", sqlBlock);
        SQLForm sqlForm = queryTemplateForm.getSQLForm();
        SQLBlock targetSQLBlock = sqlForm.getSQLBlock(sqlBlock.getOrder());

        switch (sqlBlock.getSqlBlockType()){
            case JOIN -> {
                partialQuery = "JOIN " + targetSQLBlock.getDataHolder().get(1).getTemplateName() + " ON " + targetSQLBlock.getDataHolder().stream().map(blockData -> blockData.toString()).collect(Collectors.joining(" = "));
            }
            case SELECT -> {
                partialQuery = "SELECT " +targetSQLBlock.getDataHolder().stream().map(blockData -> blockData.toString()).collect(Collectors.joining(", "));
            }
            case WHERE -> {
                partialQuery = "WHERE " + targetSQLBlock.getDataHolder().stream().map(blockData -> blockData.toString()).collect(Collectors.joining(" AND "));
            }
            case HAVING -> {
                partialQuery = "HAVING " + targetSQLBlock.getDataHolder().stream().map(blockData -> blockData.toString()).collect(Collectors.joining(" AND "));
            }
            case GROUP_BY -> {
                partialQuery = "GROUP BY (" + targetSQLBlock.getDataHolder().stream().map(blockData -> blockData.toString()).collect(Collectors.joining(", "));
            }
        }

        targetSQLBlock.setSqlQuery(partialQuery);
        sqlForm.buildSQLQuery();
        return "/template/query/add::#add_template";
    }

    @PostMapping("/convert_to_query/{memberId}")
    public String convertToQuery(@ModelAttribute SQLForm sqlForm, BindingResult bindingResult,
                                 @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member,
                                 @PathVariable String memberId, Model model){

        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        PreparedStatement preparedStatement;
        Connection connection;

        sendQueryForwardModelAttributes(model, queryTemplateForm);
        List<SQLBlock> sqlBlockList = sqlForm.getSqlBlockList();

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

            int rowCount = resultSet.getFetchSize();
            int columnCount = resultSet.getMetaData().getColumnCount();

            while(resultSet.next()){
                log.info("result={}", resultSet);
                List<Object> row = new ArrayList();
                resultList.add(row);
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                     row.add(resultSet.getObject(columnIndex));
                }
            }

            log.info("result={}", resultList);
            Member loginMember = memberRepository.findById(member.getId()).get();
            SpreadSheetSource spreadSheetSource = new SpreadSheetSource(loginMember.getFileId());

            Source<String> paramSource = new Source<>();
            paramSource.add(FILE_NAME, "QUERY_"+loginMember.getLoginId());
            Sheet sheet = spreadSheetSource.createFile(paramSource);
            String sheetRange = sheet.getProperties().getTitle() + "!A0:" + (char)('A'+columnCount) + "" + (char)('0'+rowCount);
            paramSource.add(FILE_RANGE, sheetRange);

            spreadSheetSource.createFile(paramSource);
            spreadSheetSource.writeSpreadSheetTable(paramSource, resultList);

        } catch (SQLException e) {
            sqlForm.setSqlError(e.getSQLState());
        }
        sendQueryPostModelAttributes(model, queryTemplateForm);
        return "/template/query/add";
    }

}
