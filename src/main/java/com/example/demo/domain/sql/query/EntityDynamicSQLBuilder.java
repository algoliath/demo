package com.example.demo.domain.sql.query;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.property.condition.value.ForeignKey;
import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.column.property.condition.value.ValueCondition;
import com.example.demo.domain.column.property.name.ColumnName;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.util.database.sql.QueryBuilderUtils;
import com.example.demo.util.validation.NamingUtils;
import com.example.demo.util.validation.TypeUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.example.demo.util.database.sql.QueryBuilderUtils.*;
import static com.example.demo.domain.column.property.condition.value.ValueConditionType.*;

@Component
public class EntityDynamicSQLBuilder implements DynamicSQLBuilder {

    /*CREATE TABLE SQL*/
    @Override
    public String createTableQuery(Entity entity) {

        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE ");
        query.append(entity.getName());

        // open the sql query statement
        query.append("(");
        query.append("\n");

        for (Column column : entity.getColumns()) {
            query.append(parseColumn(column));
            query.append(parseKeyConditions(column));
            query.append(",\n");
        }

        String primaryKeys = entity.getColumns().stream()
                .filter(column -> column.hasKeyCondition(KeyCondition.PRIMARY_KEY_CONDITION))
                .map(column -> column.getColumnName().getValidName())
                .collect(Collectors.joining(","));

        Map<ColumnName, ForeignKey> references = entity.getColumns().stream()
                .filter(column -> column.hasValueCondition(ValueCondition.FOREIGN_KEY_CONDITION))
                .collect(Collectors.toMap(Column::getColumnName, column -> (ForeignKey) column.getValueCondition(FOREIGN_KEY).get()));

        String constraints = entity.getColumns().stream()
                .filter(column -> !column.getValueConditions().isEmpty() && column.hasValueConditionOtherThan(FOREIGN_KEY))
                .map(QueryBuilderUtils::parseValueConditions)
                .collect(Collectors.joining(" AND "));

        if (!primaryKeys.isEmpty()) {
            query.append("PRIMARY KEY (" + primaryKeys + ")");
            query.append(",\n");
        }

        for (Entry reference : references.entrySet()) {
            ColumnName columnName = (ColumnName) reference.getKey();
            ForeignKey foreignKey = (ForeignKey) reference.getValue();
            Object updateMode = NamingUtils.removeCamelCase(foreignKey.getUpdateMode().name());
            Object deleteMode = NamingUtils.removeCamelCase(foreignKey.getDeleteMode().name());
            Object referenceTarget = NamingUtils.parseString(foreignKey.getConditionTerm().getArgument());
            query.append("FOREIGN KEY (" + columnName.getValidName() + ") " + "REFERENCES " + referenceTarget);
            if (updateMode != null) {
                query.append(" ON UPDATE " + updateMode);
            }
            if (deleteMode != null) {
                query.append(" ON DELETE " + deleteMode);
            }
            query.append(",\n");
        }

        if (!constraints.isEmpty()) {
            query.append("CHECK (" + constraints + ")");
        }

        if (query.charAt(query.length() - 2) == ',') {
            query.setLength(query.length() - 2); // remove comma and space
        }

        query.append("\n");
        // close the sql query statement
        query.append(");");
        return query.toString();
    }

    public String deleteTableQuery(Entity template) {
        String query = "DROP TABLE " + template.getName() + " IF EXISTS";
        return query;
    }

    public String insertTableQuery(Entity template) {
        List<List<Object>> values = template.getSheetTable().getValues();
        List<List<Object>> parsedValues = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        for (List<Object> row : values.subList(1, values.size())) {
            List<Object> parsedRow = new ArrayList<>();
            for (Object col : row) {
                if (TypeUtils.isCharacter(col.toString())) {
                    parsedRow.add("'" + col + "'");
                } else {
                    parsedRow.add(col);
                }
            }
            parsedValues.add(parsedRow);
        }

        for (List<Object> row : parsedValues) {
            String items = String.join(",", row.toArray(new String[row.size()]));
            builder.append("\nINSERT INTO " + template.getName() + " VALUES(" + items + ");");
        }
        return builder.toString();
    }


}
