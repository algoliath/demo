package com.example.demo.util.controller;

import com.example.demo.domain.column.property.condition.key.KeyConditionType;
import com.example.demo.domain.column.property.condition.options.OptionType;
import com.example.demo.domain.column.property.condition.value.ValueConditionType;
import com.example.demo.domain.column.property.type.ColumnType;
import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.column.form.ConditionSaveForm;
import com.example.demo.domain.column.form.JoinConditionSaveForm;
import com.example.demo.domain.data.vo.SearchForm;
import com.example.demo.domain.database.SQLOperator;
import com.example.demo.domain.database.form.*;
import com.example.demo.domain.database.model.SQLBlock;
import com.example.demo.domain.database.model.SQLBlockData;
import com.example.demo.domain.database.model.SQLBlockType;
import com.example.demo.domain.template.form.EntityTemplateForm;
import com.example.demo.domain.template.form.QueryTemplateForm;
import com.example.demo.util.validation.QueryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;

import java.util.Arrays;

@Slf4j
public class ControllerUtils {

    public static void saveEntityForwardModelAttributes(Model model, EntityTemplateForm entityTemplateForm) {
        // forms
        model.addAttribute("template", entityTemplateForm);
        model.addAttribute("templateForm", entityTemplateForm);
        model.addAttribute("joinTemplates", entityTemplateForm.getJoinTemplates());
        model.addAttribute("columnUpdateForm", new ColumnUpdateForm());
        model.addAttribute("conditionSaveForm", new ConditionSaveForm());
        model.addAttribute("joinConditionSaveForm", new JoinConditionSaveForm());
        model.addAttribute("searchForm", new SearchForm());
        // tables
        model.addAttribute("columnTypes", Arrays.stream(ColumnType.values()).toList());
        model.addAttribute("keyConditionTypes", Arrays.stream(KeyConditionType.values()).toList());
        model.addAttribute("valueConditionTypes", Arrays.stream(ValueConditionType.conditions()).toList());
        model.addAttribute("updateOptionTypes", Arrays.stream(OptionType.values()).toList());
        model.addAttribute("deleteOptionTypes", Arrays.stream(OptionType.values()).toList());
        // sources
        model.addAttribute("sourceId", entityTemplateForm.getSourceId());
    }

    public static void saveEntityPostModelAttributes(Model model, EntityTemplateForm entityTemplateForm) {
        model.addAttribute("template", entityTemplateForm);
        model.addAttribute("columnUpdateForm", new ColumnUpdateForm());
        model.addAttribute("sourceId", entityTemplateForm.getSourceId());
        log.info("entityTemplateForm={}", entityTemplateForm);
    }

    public static void sendQueryForwardModelAttributes(Model model, QueryTemplateForm queryTemplateForm) {
        // forms
        model.addAttribute("template", queryTemplateForm);
        model.addAttribute("searchForm", new SearchForm());
        model.addAttribute("columnUpdateForm", new ColumnUpdateForm());
        model.addAttribute("conditionSaveForm", new ConditionSaveForm());
        model.addAttribute("sqlForm", SQLForm.getEmptyInstance());
        model.addAttribute("sqlBlockTypes", Arrays.stream(SQLBlockType.values()).toList());
        model.addAttribute("operators", Arrays.stream(SQLOperator.mainQueryOperator()).toList());
        model.addAttribute("subQueryOperators", Arrays.stream(SQLOperator.subQueryOperator()).toList());
        model.addAttribute("sqlBlock", new SQLBlock());
        model.addAttribute("sqlBlockData", new SQLBlockData());
        // sources
        model.addAttribute("sourceId", queryTemplateForm.getSourceId());
    }

    public static void sendQueryPostModelAttributes(Model model, QueryTemplateForm queryTemplateForm) {
        model.addAttribute("template", queryTemplateForm);
        model.addAttribute("sourceId", queryTemplateForm.getSourceId());
        model.addAttribute("joinTemplates", queryTemplateForm.getJoinTemplates());
        SQLForm sqlForm = queryTemplateForm.getSQLForm();
        sqlForm.setSqlQuery(QueryUtils.convertSQLBlocks(sqlForm.getSqlBlockList()));
        sqlForm.getSqlBlockList().stream().iterator().forEachRemaining(sqlBlock -> sqlBlock.setSqlQuery(QueryUtils.convertSQLBlock(sqlBlock)));
        log.info("queryTemplateForm={}", queryTemplateForm);
    }

}
