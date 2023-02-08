package com.example.demo.util.validation;
import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.column.property.condition.key.KeyConditionType;
import com.example.demo.domain.column.property.condition.value.ValueCondition;
import com.example.demo.domain.column.property.condition.value.ValueConditionTerm;
import com.example.demo.domain.column.property.condition.value.ValueConditionType;
import com.example.demo.domain.column.property.type.ColumnType;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class SQLUtils {

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
}