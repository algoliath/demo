package com.example.demo.util.database.sql;
import com.example.demo.domain.column.Column;
import com.example.demo.domain.data.vo.template.QueryColumnUpdateForm;
import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.column.property.condition.key.KeyConditionType;
import com.example.demo.domain.column.property.condition.value.ValueConditionTerm;
import com.example.demo.domain.column.property.condition.value.ValueConditionType;
import com.example.demo.domain.column.property.type.ColumnType;
import com.example.demo.domain.data.vo.template.query.component.QueryBlock;
import com.example.demo.domain.sql.model.SQLOperator;
import com.example.demo.domain.sql.model.SQLBlockType;
import com.example.demo.domain.template.model.Query;
import com.example.demo.util.validation.NamingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.demo.domain.column.property.condition.value.ValueConditionType.FOREIGN_KEY;
import static com.example.demo.domain.column.property.condition.value.ValueConditionType.LIKE;

@Slf4j
public class QueryBuilderUtils {

    public static boolean sqlPatternMatch(String value, String sqlTerm){
        sqlTerm = ".*" + sqlTerm + ".*";
        Pattern pattern = Pattern.compile(sqlTerm, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static String parseColumn(Column column) {
        return column.getColumnName().getValidName() + " " + ColumnType.valueOf(column.getType()).getOracleSyntax();
    }

    public static String parseKeyConditions(Column column) {
        List<KeyCondition> keyConditions = column.getKeyConditions();
        StringBuilder partialSql = new StringBuilder();
        String constraints = keyConditions.stream().filter(key_cond -> key_cond.getConditionType() != KeyConditionType.PRIMARY_KEY)
                .map(keyCondition -> keyCondition.getConditionType().toString())
                .collect(Collectors.joining(" "));

        partialSql.append(constraints);
        String result = partialSql.toString().replace("_", " ");
        return (result.isEmpty())? "": " " + result;
    }

    public static String parseValueConditions(Column column) {
        return column.getValueConditions().stream()
                .filter(valueCondition -> valueCondition.getConditionType() != FOREIGN_KEY)
                .map(valueCondition -> {
                    String argument = valueCondition.getConditionTerm().getArgument();
                    String columnName = column.getColumnName().getValidName();
                    if (valueCondition.getConditionType() == LIKE) {
                        argument = "'%" + NamingUtils.parseString(argument) + "%'";
                    }
                    return columnName + " " + valueCondition.getConditionType().getOracleSyntax() + " " + argument;
                })
                .collect(Collectors.joining(" AND "));
    }

    private static String joinValConditions(ValueConditionType valueConditionType, ValueConditionTerm valueConditionTerm){
        return valueConditionType.toString() + " REFERENCES " + valueConditionTerm.getArgument() + " ";
    }

    public static String convertAllSQLBlocks(List<QueryBlock> targetQueryBlockList){
        StringBuilder queryBuilder = new StringBuilder();
        int n_select = 0;
        for(QueryBlock queryBlock : targetQueryBlockList){
            if(queryBlock.getSqlBlockType() == SQLBlockType.SELECT){
                n_select++;
            }
            queryBuilder.append(" ".repeat(Math.max(0, n_select - 1)));
            queryBuilder.append(queryBlock.getSqlQuery());
            queryBuilder.append("\n");
        }
        return queryBuilder.toString();
    }

    public static String convertSQLBlock(QueryBlock targetQueryBlock){

        if(targetQueryBlock.getSqlBlockType() == null){
            return "";
        }

        String partialQuery = "";

        switch (targetQueryBlock.getSqlBlockType()){
            case JOIN, SELECT -> partialQuery = targetQueryBlock.getDataHolder().stream()
                    .map(sqlBlockData -> convertSQLBlockData(sqlBlockData.getQueries(), targetQueryBlock.getSqlBlockType()))
                    .collect(Collectors.joining("\n"));
            case GROUP_BY -> partialQuery = targetQueryBlock.getDataHolder().stream()
                    .map(sqlBlockData -> convertSQLBlockData(sqlBlockData.getQueries(), targetQueryBlock.getSqlBlockType()))
                    .collect(Collectors.joining(","));
            case WHERE, HAVING -> partialQuery = targetQueryBlock.getDataHolder().stream()
                    .map(sqlBlockData -> convertSQLBlockData(sqlBlockData.getQueries(), targetQueryBlock.getSqlBlockType()))
                    .collect(Collectors.joining(" AND "));
        }

        switch (targetQueryBlock.getSqlBlockType()){
            case SELECT, WHERE, HAVING -> {
                if(StringUtils.hasText(partialQuery)){
                   partialQuery = targetQueryBlock.getSqlBlockType() + " " + partialQuery;
                }
            }
            case GROUP_BY -> {
                if(StringUtils.hasText(partialQuery)){
                    partialQuery = targetQueryBlock.getSqlBlockType() + " (" + partialQuery + ")";
                }
            }
            case JOIN -> {
            }
        }

        return partialQuery;
    }

    public static String convertSQLBlockData(List<Query> queries, SQLBlockType sqlBlockType) {
        String connector = "";
        switch (sqlBlockType) {
            case SELECT -> {
                connector = " UNION ";
            }
            case GROUP_BY -> connector = ", ";
            case WHERE, HAVING -> {
                connector = " AND ";
            }
            case JOIN -> {
                connector = " JOIN ";
            }
        }
        return queries.stream().map(query -> convertQuery(query, sqlBlockType)).collect(Collectors.joining(connector));
    }

    public static String convertQuery(Query query, SQLBlockType sqlBlockType){
        List<QueryColumnUpdateForm> targetColumns = query.getTargetColumns();
        switch (sqlBlockType) {
            case SELECT -> {
                if (query.getAlias() != null) {
                    return targetColumns.stream()
                                    .map(column -> query.getName() + "." + column.getName())
                                    .collect(Collectors.joining(", ")) + " FROM (" + query.getName() + ") " + query.getAlias();
                }
                return targetColumns.stream()
                                .map(column -> query.getName() + "." + column.getName())
                                .collect(Collectors.joining(", "))  + " FROM " + query.getName();
            }
            case GROUP_BY -> {
                return targetColumns.stream().
                        map(column -> query.getName() + "." + column.getName()).
                        collect(Collectors.joining(","));
            }
            case WHERE, HAVING -> {
                if (query.getAlias() != null) {
                    return targetColumns.stream().
                            map(column -> query.getName() + "." + column.getName() + " " + column.getOperator().getSign() + "(" + query.getName() + ")").
                            collect(Collectors.joining(" AND "));
                }
                return targetColumns.stream().filter(column -> SQLOperator.hasOperator(column.getOperator())).map(column -> query.getName() + "." + column + " " + column.getOperator().getSign() + " " + column.getOperand()).collect(Collectors.joining(" AND "));
            }
            case JOIN -> {
                return targetColumns.stream().map(column -> query.getName() + "." + column).collect(Collectors.joining(" JOIN "));
            }
            default -> {
                return "";
            }
        }
    }

}