package com.example.demo.web.controller;

import com.example.demo.domain.data.vo.template.QueryColumnUpdateForm;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.data.vo.template.query.*;
import com.example.demo.domain.data.vo.template.query.component.QueryBlockData;
import com.example.demo.domain.sql.model.SQLOperator;
import com.example.demo.domain.data.vo.template.query.component.QueryBlock;
import com.example.demo.domain.sql.model.SQLBlockType;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import com.example.demo.domain.source.datasource.SpreadSheetSource;
import com.example.demo.domain.template.model.Query;
import com.example.demo.domain.template.service.TemplateService;
import com.example.demo.util.Source;
import com.example.demo.util.template.TemplateFormProvider;
import com.example.demo.util.database.sql.QueryBuilderUtils;
import com.example.demo.web.session.SessionConst;
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
import java.util.stream.Collectors;

import static com.example.demo.domain.source.datasource.SpreadSheetSource.*;
import static com.example.demo.util.database.DBConnectionUtils.getConnection;

@Slf4j
@Controller
@RequestMapping("/template/query")
public class QueryController {

    private final MemberRepository memberRepository;
    private final TemplateFormProvider templateFormProvider;
    private final TemplateService templateService;
    private static final String QUERY_VIEW_URL = "template/query/add::#query-column";

    @Autowired
    public QueryController(MemberRepository memberRepository, TemplateFormProvider templateFormProvider, TemplateService templateService) {
        this.memberRepository = memberRepository;
        this.templateFormProvider = templateFormProvider;
        this.templateService = templateService;
    }

    @DeleteMapping(value = "/delete_template/{memberId}")
    public String deleteTemplate(@ModelAttribute QueryBlockForm queryBlockForm, @PathVariable String memberId, Model model) {
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        QueryBlockData queryBlockData = queryTemplateForm.getQueryBuilderForm().getSQLBlock(queryBlockForm.getSqlBlockOrder()).getSQLBlockData(queryBlockForm.getSqlDataOrder());
        String queryName = queryBlockForm.getTemplateName();
        queryBlockData.deleteQuery(queryName);
        return "template/query/add";
    }

    @PostMapping(value = "/add_template/{memberId}")
    public String addTemplate(@ModelAttribute QueryBlockForm queryBlockForm, @PathVariable String memberId, Model model) {
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        QueryBuilderForm queryBuilderForm = queryTemplateForm.getQueryBuilderForm();
        String templateName = queryBlockForm.getTemplateName();

        Integer blockOrder = queryBlockForm.getSqlBlockOrder();
        Integer dataOrder = queryBlockForm.getSqlDataOrder();
        Integer queryOrder = queryBlockForm.getQueryOrder();

        QueryBlockData target = queryBuilderForm.getSQLBlock(queryBlockForm.getSqlBlockOrder()).getSQLBlockData(queryBlockForm.getSqlDataOrder());
        Query targetQuery = target.getQuery(templateName).orElse(null);

        // 메인 쿼리 타입
        if (targetQuery == null) {
            Query query = templateService.findQueriesByName(templateName).get(0);
            List<QueryColumnUpdateForm> queryColumnUpdateFormList = templateService.findColumnsByTemplateId(query.getId()).stream()
                    .map(column -> new QueryColumnUpdateForm(column.getColumnName().getValidName()))
                    .collect(Collectors.toList());
            target.addQuery(new Query(query, queryColumnUpdateFormList));
        }
        // 서브 쿼리 타입
        else if (targetQuery.getSubQueryMark() != null && targetQuery.getSubQueryMark().booleanValue()) {
            targetQuery.setAlias("SUB_QUERY" + "_" + blockOrder + "_" + dataOrder + "_" + queryOrder);
        }

        return QUERY_VIEW_URL;
    }

    @PostMapping("/add_column_form/{memberId}")
    public String addColumnForm(@ModelAttribute QueryBlockForm queryBlockForm, BindingResult bindingResult,
                                @PathVariable String memberId, Model model){
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        QueryBlock queryBlock = queryTemplateForm.getSQLBlock(queryBlockForm.getSqlBlockOrder());
        QueryBlockData target = queryBlock.getDataHolder().get(queryBlockForm.getSqlDataOrder());
        String queryName = queryBlockForm.getTemplateName();

        target.getQuery(queryName).orElseThrow(() -> {
            throw new IllegalArgumentException("찾고자 하는 쿼리 템플릿 이름이 존재하지 않습니다");
        });

        Query query = target.getQuery(queryName).get();
        query.addColumn(new QueryColumnUpdateForm(""));
        return QUERY_VIEW_URL;
    }

    @PostMapping("/update_columns/{memberId}")
    public String updateColumns(@ModelAttribute QueryBlockForm queryBlockForm, BindingResult bindingResult,
                                @PathVariable String memberId, Model model) {

        log.info("queryBlockForm={}", queryBlockForm);
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        QueryBlock queryBlock = queryTemplateForm.getSQLBlock(queryBlockForm.getSqlBlockOrder());
        QueryBlockData target = queryBlock.getDataHolder().get(queryBlockForm.getSqlDataOrder());
        String queryName = queryBlockForm.getTemplateName();

        target.getQuery(queryName).orElseThrow(() -> {
            throw new IllegalArgumentException("찾고자 하는 쿼리 템플릿 이름이 존재하지 않습니다");
        });

        Query query = target.getQuery(queryName).get();
        List<String> targetColumnNames = queryBlockForm.getColumns();
        List<QueryColumnUpdateForm> targetColumns = query.getColumns().stream()
                .filter(queryColumn -> targetColumnNames.contains(queryColumn.getName()))
                .collect(Collectors.toList());
        List<SQLOperator> targetOperators = queryBlockForm.getOperators().stream()
                .map(operator -> SQLOperator.valueOf(operator))
                .collect(Collectors.toList());

        query.setTargetColumns(targetColumns);
        model.addAttribute("sqlBlockForm", queryBlockForm);
        switch (queryBlock.getSqlBlockType()) {
            case WHERE, HAVING -> {
                for (int i = 0; i < targetColumns.size(); i++) {
                    QueryColumnUpdateForm queryColumnUpdateForm = targetColumns.get(i);
                    queryColumnUpdateForm.setOperator(targetOperators.get(i));
                }
            }
        }
        return QUERY_VIEW_URL;
    }

    @GetMapping("/add_query_data/{memberId}")
    public String addData(@Validated @ModelAttribute QueryBlockForm queryBlockForm, BindingResult bindingResult,
                          @PathVariable String memberId, Model model) {
        Integer sqlBlockOrder = queryBlockForm.getSqlBlockOrder();
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        if (bindingResult.hasErrors()) {
            return QUERY_VIEW_URL;
        }
        QueryBlock queryBlock = queryTemplateForm.getQueryBuilderForm().getQueryBlockList().get(sqlBlockOrder);
        QueryBlockData addQueryBlockData = new QueryBlockData(queryBlock.getSqlBlockType(), queryBlockForm.getSqlBlockOrder(), queryBlockForm.getSqlDataOrder() + 1);
        queryBlock.addData(addQueryBlockData);
        return QUERY_VIEW_URL;
    }

    @GetMapping("/delete_query_data/{memberId}")
    public String removeData(@Validated @ModelAttribute QueryBlockForm queryBlockForm, BindingResult bindingResult,
                             @PathVariable String memberId, Model model) {
        if (bindingResult.hasErrors()) {
            return QUERY_VIEW_URL;
        }
        int sqlBlockOrder = queryBlockForm.getSqlBlockOrder();
        int sqlDataOrder = queryBlockForm.getSqlDataOrder();
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        List<QueryBlock> queryBlockList = queryTemplateForm.getQueryBuilderForm().getQueryBlockList();
        QueryBlock queryBlock = queryBlockList.get(sqlBlockOrder);
        queryBlock.removeData(sqlDataOrder);
        return QUERY_VIEW_URL;
    }

    @GetMapping("/add_block/{memberId}")
    public String addBlock(@ModelAttribute QueryBlockForm queryBlockForm, BindingResult bindingResult,
                           @PathVariable String memberId, Model model) {
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        List<QueryBlock> queryBlockList = queryTemplateForm.getQueryBuilderForm().getQueryBlockList();
        int sqlBlockOrder = queryBlockForm.getSqlBlockOrder();
        queryBlockList.add(sqlBlockOrder, new QueryBlock(sqlBlockOrder + 1));
        return QUERY_VIEW_URL;
    }

    @PostMapping("/add_sub_query/{memberId}")
    public String addSubQuery(@ModelAttribute QueryBlockForm queryBlockForm, BindingResult bindingResult,
                              @PathVariable String memberId, Model model) {

        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        List<QueryBlock> queryBlockList = queryTemplateForm.getQueryBuilderForm().getQueryBlockList();

        List<QueryBlock> targetQueryBlockList = queryBlockForm.getIndices()
                .stream()
                .map(queryBlockList::get)
                .collect(Collectors.toList());

        if (targetQueryBlockList.isEmpty()) {
            return QUERY_VIEW_URL;
        }

        for (QueryBlock targetQueryBlock : targetQueryBlockList) {
            queryBlockList.remove(targetQueryBlock);
        }

        Query subQuery = new Query();
        subQuery.setName(QueryBuilderUtils.convertAllSQLBlocks(targetQueryBlockList));

        for (QueryBlock queryBlock : targetQueryBlockList) {
            if (queryBlock.getSqlBlockType() == SQLBlockType.SELECT) {
                for (QueryBlockData queryBlockData : queryBlock.getDataHolder()) {
                    List<QueryColumnUpdateForm> columns = new ArrayList<>();
                    Query query = queryBlockData.getQuery(0);
                    for (QueryColumnUpdateForm column : query.getColumns()) {
                        columns.add(column);
                    }
                    query.setColumns(columns);
                }
            }
        }

        int startIndex = queryBlockForm.getIndices().get(0);
        if (queryBlockList.get(startIndex).getSqlBlockType() != SQLBlockType.SELECT) {
            throw new IllegalStateException("서브쿼리는 SELECT 블록으로 시작해야 합니다");
        }

        QueryBlock addQueryBlock = new QueryBlock(SQLBlockType.SELECT, startIndex);
        QueryBlockData addQueryBlockData = new QueryBlockData(SQLBlockType.SELECT, startIndex, 0);
        subQuery.setSubQueryMark(true);

        addQueryBlockData.addQuery(subQuery);
        addQueryBlock.addData(addQueryBlockData);
        queryBlockList.add(startIndex, addQueryBlock);

        return QUERY_VIEW_URL;
    }

    @GetMapping("/delete_block/{memberId}")
    public String removeBlock(@Validated @ModelAttribute QueryBlockForm queryBlockForm, BindingResult bindingResult,
                              @PathVariable String memberId, Model model) {
        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        if (bindingResult.hasErrors()) {
            return QUERY_VIEW_URL;
        }
        List<QueryBlock> queryBlockList = queryTemplateForm.getQueryBuilderForm().getQueryBlockList();
        int sqlBlockOrder = queryBlockForm.getSqlBlockOrder();
        queryBlockList.remove(sqlBlockOrder);

        return QUERY_VIEW_URL;
    }

    @PostMapping("/edit_block/{memberId}")
    public String editBlock(@Validated @ModelAttribute QueryBlockForm queryBlockForm, BindingResult bindingResult,
                            @PathVariable String memberId, Model model) {
        QueryTemplateForm templateForm = templateFormProvider.getQueryTemplateForm(memberId);
        if (bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            return QUERY_VIEW_URL;
        }
        List<QueryBlock> queryBlockList = templateForm.getQueryBuilderForm().getQueryBlockList();
        QueryBlock targetQueryBlock = queryBlockList.get(queryBlockForm.getSqlBlockOrder());
        targetQueryBlock.changeSQLBlockType(queryBlockForm.getSqlBlockType());
        targetQueryBlock = new QueryBlock();
        return QUERY_VIEW_URL;
    }

    @PostMapping("/execute_query/{memberId}")
    public String executeQuery(@ModelAttribute QueryBlockForm queryBlockForm, BindingResult bindingResult,
                               @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                               @PathVariable String memberId, Model model) {

        QueryTemplateForm queryTemplateForm = templateFormProvider.getQueryTemplateForm(memberId);
        QueryBuilderForm queryBuilderForm = queryTemplateForm.getQueryBuilderForm();
        PreparedStatement preparedStatement;
        Connection connection;

        List<QueryBlock> queryBlockList = queryTemplateForm.getQueryBuilderForm().getQueryBlockList();

        try {
            // 쿼리 생성
            StringBuilder queryBuilder = new StringBuilder();
            for (QueryBlock queryBlock : queryBlockList) {
                queryBuilder.append(queryBlock.getSqlQuery());
                queryBuilder.append(" ");
            }

            // 쿼리 실행 (JDBC)
            String query = queryBuilder.toString();
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<List<Object>> resultList = new ArrayList<>();

            int rowCount = 0;
            int columnCount = resultSet.getMetaData().getColumnCount();

            while (resultSet.next()) {
                queryBuilderForm.getQueryBlockList().stream().iterator().forEachRemaining(queryBlock -> queryBlock.setSqlQuery(QueryBuilderUtils.convertSQLBlock(queryBlock)));
                log.info("result={}", resultSet);
                List<Object> row = new ArrayList<>();
                resultList.add(row);
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    row.add(resultSet.getObject(columnIndex));
                }
                rowCount++;
            }

            // 쿼리 결과를 스프레드시트 파일에 저장
            log.info("result={}", resultList);
            Member loginMember = memberRepository.findById(member.getId()).get();
            SpreadSheetSource spreadSheetSource = new SpreadSheetSource(loginMember.getFileId());

            Source<String> paramSource = new Source<>();
            paramSource.add(FILE_NAME, "QUERY_" + loginMember.getId());
            List<File> files = spreadSheetSource.getFiles(paramSource);
            File targetFile = (!files.isEmpty()) ? files.get(0) : spreadSheetSource.createFile(paramSource);

            Sheet sheet = spreadSheetSource.get(targetFile);
            String sheetRange = sheet.getProperties().getTitle() + "!A1:" + (char) ('A' + columnCount) + "" + rowCount;
            paramSource.add(FILE_RANGE, sheetRange);
            spreadSheetSource.publish(paramSource);

            spreadSheetSource.clear(paramSource, sheetRange);
            spreadSheetSource.write(paramSource, resultList);
            queryTemplateForm.setSourceId(targetFile.getId());
            queryTemplateForm.setSpreadSheetTable(new SpreadSheetTable(sheetRange, resultList));

        } catch (SQLException e) {
            queryBuilderForm.setSqlError(e.getSQLState());
        }

        return "/template/query/add";
    }

}
