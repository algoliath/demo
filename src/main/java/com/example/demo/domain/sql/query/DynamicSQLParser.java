package com.example.demo.domain.sql.query;

import java.io.StringReader;
import java.util.List;
import java.util.Stack;

import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.service.TemplateService;
import com.example.demo.util.Source;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicSQLParser {

    private final TemplateService templateService;

    public void parseQuery(String sql) throws JSQLParserException {
        Select select = (Select) CCJSqlParserUtil.parse(new StringReader(sql));
        if (select.getSelectBody() instanceof PlainSelect) {
            parseQueryBlock(select.getSelectBody(), new Source<>());
        } else {
            throw new IllegalArgumentException("Unsupported SELECT body: " + select.getSelectBody());
        }
    }

    private void parseQueryBlock(Object queryBlock, Source<String> errors) {
        if (queryBlock instanceof FromItem) {
            FromItem fromItem = (FromItem) queryBlock;
            if (fromItem instanceof Table) {
                Table table = (Table) fromItem;
                String tableName = table.getName();
                List<Entity> entitiesByName = templateService.findEntitiesByName(tableName);
                if(entitiesByName.isEmpty()){
                    errors.add("invalid.table.name", tableName + " doesn't exist");
                }
            }
        }

        if (queryBlock instanceof Expression) {
            BinaryExpression binaryExpression = (BinaryExpression) queryBlock;
            Expression leftExpression = binaryExpression.getLeftExpression();
            if (leftExpression instanceof Column) {
                Column column = (Column) leftExpression;
                log.info("column={}", column);
                // do something with the column name
            }
        }

        if (queryBlock instanceof GroupByElement) {
            GroupByElement groupByElement = (GroupByElement) queryBlock;
            ExpressionList groupByExpressionList = groupByElement.getGroupByExpressionList();
            for (java.lang.Object o : groupByExpressionList.getExpressions()) {
                Column column = (Column) o;
                log.info("column={}", column);
            }
        }

        if (queryBlock instanceof List) {
            List<?> list = (List<?>) queryBlock;
            if (!list.isEmpty() && list.get(0) instanceof OrderByElement) {
                // obj is a non-empty list of OrderByElement objects
                List<OrderByElement> orderByList = (List<OrderByElement>) queryBlock;
                // do something with orderByList
                log.info("orderByList={}", orderByList);
            }
        }

        if (queryBlock instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) queryBlock;
            List<SelectItem> selectItems = plainSelect.getSelectItems();
            for (SelectItem selectItem : selectItems) {
                log.info("selectItem={}", selectItem);
            }
            parseQueryBlock(plainSelect.getFromItem(), errors);
            parseQueryBlock(plainSelect.getWhere(), errors);
            parseQueryBlock(plainSelect.getGroupBy(), errors);
            parseQueryBlock(plainSelect.getHaving(), errors);
            parseQueryBlock(plainSelect.getOrderByElements(), errors);
        } else if (queryBlock instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) queryBlock;
            parseQueryBlock(subSelect.getSelectBody(), errors);
        } else if (queryBlock instanceof SetOperationList) {
            SetOperationList setOperationList = (SetOperationList) queryBlock;
            List<SelectBody> selectBodies = setOperationList.getSelects();
            for (SelectBody body : selectBodies) {
                parseQueryBlock(body, errors);
            }
        } else if (queryBlock instanceof WithItem) {
            WithItem withItem = (WithItem) queryBlock;
            SubSelect subSelect = withItem.getSubSelect();
            SelectBody subSelectBody = subSelect.getSelectBody();
            parseQueryBlock(subSelectBody, errors);
        }
    }

}
