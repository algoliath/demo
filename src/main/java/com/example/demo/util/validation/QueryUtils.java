package com.example.demo.util.validation;
import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.column.property.condition.key.KeyConditionType;
import com.example.demo.domain.column.property.condition.value.ValueCondition;
import com.example.demo.domain.column.property.condition.value.ValueConditionTerm;
import com.example.demo.domain.column.property.condition.value.ValueConditionType;
import com.example.demo.domain.column.property.type.ColumnType;
import com.example.demo.domain.database.model.SQLBlock;
import com.example.demo.domain.database.model.SQLBlockData;
import com.example.demo.domain.database.model.SQLBlockType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
            partialSql.append("CONSTRAINT " + column.getColumnName().getValidName().replace(" ", "") + " CHECK " + constraints);
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
            if(sqlBlock.getSqlBlockType() == SQLBlockType.SELECT){
                n_select++;
            }
            for(int i=1; i<n_select; i++){
                queryBuilder.append(" ");
            }
            queryBuilder.append(sqlBlock.getSqlQuery());
            queryBuilder.append("\n");
        }
        return queryBuilder.toString();
    }

    public static String convertSQLBlock(SQLBlock targetSQLBlock){

        if(targetSQLBlock.getSqlBlockType() == null){
            return "";
        }

        String partialQuery = "";

        switch (targetSQLBlock.getSqlBlockType()){
            case JOIN -> {
                List<String> joins = new ArrayList<>();
                for(int i=0; i+1<targetSQLBlock.getDataHolder().size(); i++){
                    SQLBlockData prev = targetSQLBlock.getDataHolder().get(i);
                    SQLBlockData current = targetSQLBlock.getDataHolder().get(i+1);
                    if(prev.getTemplateName()!=null && current.getTemplateName()!=null){
                        joins.add("JOIN " + current.getTemplateName() + " ON " + prev.getTargetColumn(0) + "=" + current.getTargetColumn(0));
                    }
                }
                partialQuery = joins.stream().collect(Collectors.joining("AND \n"));
            }
            case SELECT -> {
                partialQuery = targetSQLBlock.getDataHolder().stream().filter(sqlBlockData -> sqlBlockData.getTemplateName()!=null)
                                .map(sqlBlockData -> (sqlBlockData.getTemplateAlias() != null)?
                                        (sqlBlockData + " FROM (" + sqlBlockData.getTemplateName() + ") " + sqlBlockData.getTemplateAlias())
                                        :(sqlBlockData + " FROM " +  sqlBlockData.getTemplateName()))
                                .collect(Collectors.joining());
            }
            case GROUP_BY -> {
                partialQuery = targetSQLBlock.getDataHolder().stream().filter(sqlBlockData -> sqlBlockData.getTemplateName()!=null)
                        .map(sqlBlockData ->  sqlBlockData.getTemplateName()).collect(Collectors.joining(", "));
            }
            case WHERE, HAVING -> {
                partialQuery = targetSQLBlock.getDataHolder().stream().filter(sqlBlockData -> sqlBlockData.getTemplateName()!=null)
                        .map(blockData -> blockData.toString()).collect(Collectors.joining(" AND "));
            }
            case SUBQUERY -> {
                partialQuery = targetSQLBlock.getSqlQuery();
            }
        }

        switch (targetSQLBlock.getSqlBlockType()){
            case SELECT, WHERE, HAVING -> {
                if(StringUtils.hasText(partialQuery)){
                   partialQuery = targetSQLBlock.getSqlBlockType() + " " + partialQuery;
                }
            }
            case GROUP_BY -> {
                if(StringUtils.hasText(partialQuery)){
                    partialQuery = targetSQLBlock.getSqlBlockType() + " (" + partialQuery + ")";
                }
            }
        }

        return partialQuery;
    }

}