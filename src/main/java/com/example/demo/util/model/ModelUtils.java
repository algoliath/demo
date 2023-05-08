package com.example.demo.util.model;

import com.example.demo.domain.column.property.condition.key.KeyConditionType;
import com.example.demo.domain.column.property.condition.options.OptionType;
import com.example.demo.domain.column.property.condition.value.ValueConditionType;
import com.example.demo.domain.column.property.type.ColumnType;
import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.column.form.ConditionSaveForm;
import com.example.demo.domain.column.form.JoinConditionSaveForm;
import com.example.demo.domain.data.vo.template.entity.EntitySearchForm;
import com.example.demo.domain.data.vo.template.query.QueryBlockForm;
import com.example.demo.domain.sql.model.SQLOperator;
import com.example.demo.domain.sql.model.SQLBlockType;
import com.example.demo.domain.data.vo.template.entity.EntityTemplateForm;
import com.example.demo.domain.data.vo.template.query.QueryTemplateForm;
import com.example.demo.domain.data.vo.template.TemplateForm;
import com.example.demo.domain.template.type.TemplateType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;

import java.util.Arrays;

@Slf4j
public class ModelUtils {

    public static void sendForwardModelAttributes(Model model, TemplateForm templateForm) {
        switch(TemplateType.valueOf(templateForm.getType())){
            case ENTITY -> {
                EntityTemplateForm entityTemplateForm = (EntityTemplateForm) templateForm;
                // forms
                model.addAttribute("template", entityTemplateForm);
                model.addAttribute("joinTemplates", entityTemplateForm.getJoinTemplates());
                model.addAttribute("columnUpdateForm", new ColumnUpdateForm());
                model.addAttribute("conditionSaveForm", new ConditionSaveForm());
                model.addAttribute("joinConditionSaveForm", new JoinConditionSaveForm());
                model.addAttribute("entitySearchForm", new EntitySearchForm());
                // tables
                model.addAttribute("columnTypes", Arrays.stream(ColumnType.values()).toList());
                model.addAttribute("keyConditionTypes", Arrays.stream(KeyConditionType.values()).toList());
                model.addAttribute("valueConditionTypes", Arrays.stream(ValueConditionType.conditions()).toList());
                model.addAttribute("updateOptionTypes", Arrays.stream(OptionType.values()).toList());
                model.addAttribute("deleteOptionTypes", Arrays.stream(OptionType.values()).toList());
                // sources
                model.addAttribute("sourceId", entityTemplateForm.getSourceId());
            }
            case QUERY -> {
                QueryTemplateForm queryTemplateForm = (QueryTemplateForm) templateForm;
                // forms
                model.addAttribute("template", queryTemplateForm);
                model.addAttribute("sqlBlockTypes", Arrays.stream(SQLBlockType.values()).toList());
                model.addAttribute("operators", Arrays.stream(SQLOperator.mainQueryOperator()).toList());
                model.addAttribute("subQueryOperators", Arrays.stream(SQLOperator.subQueryOperator()).toList());
                model.addAttribute("queryBlockForm", new QueryBlockForm());
                // sources
                model.addAttribute("sourceId", queryTemplateForm.getSourceId());
            }
        }
    }

    public static void sendPostModelAttributes(Model model, TemplateForm templateForm) {
        switch(TemplateType.valueOf(templateForm.getType())) {
            case ENTITY -> {
                EntityTemplateForm entityTemplateForm = (EntityTemplateForm) templateForm;
                model.addAttribute("template", entityTemplateForm);
                model.addAttribute("columnUpdateForm", new ColumnUpdateForm());
                model.addAttribute("sourceId", entityTemplateForm.getSourceId());
            }
            case QUERY -> {
                QueryTemplateForm queryTemplateForm = (QueryTemplateForm) templateForm;
                model.addAttribute("template", queryTemplateForm);
                model.addAttribute("sourceId", queryTemplateForm.getSourceId());
            }
        }
    }

}
