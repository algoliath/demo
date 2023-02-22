package com.example.demo.util.validation;
import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.column.property.condition.key.KeyConditionType;
import com.example.demo.domain.column.property.condition.value.ValueCondition;
import com.example.demo.domain.column.property.condition.value.ValueConditionTerm;
import com.example.demo.domain.column.property.condition.value.ValueConditionType;
import com.example.demo.domain.column.property.type.ColumnType;
import com.example.demo.domain.database.SQLOperator;
import com.example.demo.domain.database.model.SQLBlock;
import com.example.demo.domain.database.model.SQLBlockType;
import com.example.demo.domain.template.model.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class QueryUtils {

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

    public static String parseValConditions(Column column) {
        List<ValueCondition> valueConditions = column.getValueConditions();
        StringBuilder partialSql = new StringBuilder();
        String constraints = valueConditions.stream()
                .filter(val_cond -> val_cond.getConditionType() != ValueConditionType.FOREIGN_KEY)
                .map(valueCondition -> "VALUE " + valueCondition.getConditionType().getOracleSyntax() + " " + valueCondition.getConditionTerm().getArgument())
                .collect(Collectors.joining(" AND "));

        if(!constraints.isEmpty()){
            partialSql.append("CONSTRAINT ").append(column.getColumnName().getValidName().replace(" ", "")).append(" CHECK ").append(constraints);
        }

        String result = partialSql.toString().replaceAll("_", " ");
        return (result.isEmpty())? ",\n": " " + result + ",\n";
    }

    private static String joinValConditions(ValueConditionType valueConditionType, ValueConditionTerm valueConditionTerm){
        return valueConditionType.toString() + " REFERENCES " + valueConditionTerm.getArgument() + " ";
    }

    public static String parseSQL(String sql) {
        sql = sql.replaceAll("\n", " ");
        String[] keywords = sql.split("");
        StringBuilder sb = new StringBuilder();
        for(String keyword: keywords){
            if(keyword.equals(" ") || keyword.equals("\n")){
                continue;
            }
            sb.append(" ");
            sb.append(keyword);
        }
        log.info("sql={}", sql);
        return sb.substring(1);
    }


    public static String convertSQLBlocks(List<SQLBlock> targetSQLBlockList){
        StringBuilder queryBuilder = new StringBuilder();
        int n_select = 0;
        for(SQLBlock sqlBlock: targetSQLBlockList){
            if(sqlBlock.getSQLBlockType() == SQLBlockType.SELECT){
                n_select++;
            }
            queryBuilder.append(" ".repeat(Math.max(0, n_select - 1)));
            queryBuilder.append(sqlBlock.getSqlQuery());
            queryBuilder.append("\n");
        }
        return queryBuilder.toString();
    }

    public static String convertSQLBlock(SQLBlock targetSQLBlock){

        if(targetSQLBlock.getSQLBlockType() == null){
            return "";
        }

        String partialQuery = "";

        switch (targetSQLBlock.getSQLBlockType()){
            case JOIN, SELECT -> partialQuery = targetSQLBlock.getDataHolder().stream()
                    .map(sqlBlockData -> convertSQLBlockData(sqlBlockData.getQueries(), targetSQLBlock.getSQLBlockType()))
                    .collect(Collectors.joining("\n"));
            case GROUP_BY -> partialQuery = targetSQLBlock.getDataHolder().stream()
                    .map(sqlBlockData -> convertSQLBlockData(sqlBlockData.getQueries(), targetSQLBlock.getSQLBlockType()))
                    .collect(Collectors.joining(","));
            case WHERE, HAVING -> partialQuery = targetSQLBlock.getDataHolder().stream()
                    .map(sqlBlockData -> convertSQLBlockData(sqlBlockData.getQueries(), targetSQLBlock.getSQLBlockType()))
                    .collect(Collectors.joining(" AND "));
        }

        switch (targetSQLBlock.getSQLBlockType()){
            case SELECT, WHERE, HAVING -> {
                if(StringUtils.hasText(partialQuery)){
                   partialQuery = targetSQLBlock.getSQLBlockType() + " " + partialQuery;
                }
            }
            case GROUP_BY -> {
                if(StringUtils.hasText(partialQuery)){
                    partialQuery = targetSQLBlock.getSQLBlockType() + " (" + partialQuery + ")";
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
            case GROUP_BY -> {
                connector = ", ";
            }
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
        List<Column> targetColumns = query.getTargetColumns();
        switch (sqlBlockType) {
            case SELECT -> {
                if (query.getAlias() != null) {
                    return targetColumns.stream().
                            map(column -> query.getName() + "." + column + " FROM (" + query.getName() + ") " + query.getAlias()).
                            collect(Collectors.joining(", "));
                }
                return targetColumns.stream().
                        map(column -> query.getName() + "." + column + " FROM " + query.getName()).
                        collect(Collectors.joining(", "));
            }
            case GROUP_BY -> {
                return targetColumns.stream().
                        map(column -> query.getName() + "." + column).
                        collect(Collectors.joining(","));
            }
            case WHERE, HAVING -> {
                if (query.getAlias() != null) {
                    return targetColumns.stream().
                            map(column -> query.getOperator() + "(" + query.getName() + ")").
                            collect(Collectors.joining(" AND "));
                }
                return targetColumns.stream().filter(column -> SQLOperator.hasOperator(query.getOperator())).map(column -> query.getName() + "." + column + " " + SQLOperator.valueOf(query.getOperator()).getSign() + " " + query.getOperand()).collect(Collectors.joining(" AND "));
            }
            case JOIN -> {
                return targetColumns.stream().map(column -> query.getName() + "." + column).collect(Collectors.joining(""));
            }
            default -> {
                return "";
            }
        }
    }

}