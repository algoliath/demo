package com.example.demo.web.controller;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.property.condition.options.OptionType;
import com.example.demo.domain.column.property.condition.value.ForeignKey;
import com.example.demo.domain.column.property.name.ColumnName;
import com.example.demo.domain.data.vo.template.entity.EntitySearchForm;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import com.example.demo.domain.source.datasource.wrapper.DataSourceId;
import com.example.demo.domain.data.dto.EntityDTO;
import com.example.demo.domain.validation.EntityValidator;
import com.example.demo.util.Source;
import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.column.property.condition.value.ValueCondition;
import com.example.demo.domain.column.form.*;
import com.example.demo.domain.column.property.type.ColumnType;
import com.example.demo.domain.column.ColumnTypeConverter;
import com.example.demo.domain.template.service.TemplateService;
import com.example.demo.domain.source.datasource.SpreadSheetSource;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.data.vo.template.entity.EntityTemplateForm;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.util.message.MessageConverterUtils;
import com.example.demo.util.template.TemplateFormProvider;
import com.example.demo.web.session.SessionConst;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

import static com.example.demo.domain.source.datasource.DataSource.*;

@Slf4j
@Controller
@RequestMapping("/template/entity")
@RequiredArgsConstructor
public class EntityController {

    private final TemplateService templateService;
    private final ColumnTypeConverter converter;
    private final TemplateFormProvider templateFormProvider;
    private final EntityValidator entityValidator;

//      @InitBinder
//      public void init(WebDataBinder webDataBinder){
//           log.info("webDataBinder={}", webDataBinder);
//           webDataBinder.addValidators(entityValidator);
//      }

    @PostMapping("/load_file/{memberId}")
    public String loadFile(@ModelAttribute("searchForm") EntitySearchForm entitySearchForm, BindingResult bindingResult,
                           @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                           @PathVariable String memberId, Model model) {

        String credentials = member.getFileId();
        String fileName = entitySearchForm.getName();

        SpreadSheetSource spreadSheetSource = new SpreadSheetSource(credentials);
        EntityTemplateForm entityTemplateForm = templateFormProvider.getEntityTemplateForm(memberId);

        entityTemplateForm.setSpreadSheetTitle(fileName);
        Source<String> paramSource = new Source<>(Arrays.asList(FILE_NAME), Arrays.asList(fileName));

        DataSourceId dataSourceId = spreadSheetSource.publish(paramSource);
        entityTemplateForm.setSourceId(dataSourceId.getOriginalFileId());
        log.info("-----------[GET END]--------------\n");
        return "template/entity/add";
    }

    @PostMapping("/edit_template/{memberId}")
    public String editTemplate(@ModelAttribute("template") EntityTemplateForm entityTemplateForm, BindingResult bindingResult,
                               @PathVariable String memberId, Model model) {
        EntityTemplateForm templateForm = templateFormProvider.getEntityTemplateForm(memberId);
        templateForm.setSpreadSheetTitle(entityTemplateForm.getSpreadSheetTitle());
        model.addAttribute("template", templateForm);
        model.addAttribute("columns", templateForm.getColumnUpdateForms().getColumnUpdateFormList());
        return "template/entitiy/update";
    }


    @PostMapping("/sync_entity/{memberId}")
    public String syncTemplate(@Validated @ModelAttribute("template") EntityTemplateForm templateForm, BindingResult bindingResult,
                               @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                               @PathVariable String memberId, Model model) {

        Source<List<String>> errors = new Source<>();
        String credentials = member.getFileId();
        SpreadSheetSource spreadSheetSource = new SpreadSheetSource(credentials);
        EntityTemplateForm entityTemplateForm = templateFormProvider.getEntityTemplateForm(memberId);
        String spreadSheetTitle = templateForm.getSpreadSheetTitle();
        String spreadSheetRange = templateForm.getSpreadSheetRange();

        entityTemplateForm.setName(templateForm.getName());
        entityTemplateForm.attachFile(credentials, spreadSheetTitle, spreadSheetRange, errors);
        ColumnUpdateForms columnUpdateForms = new ColumnUpdateForms();

        if (!StringUtils.hasText(entityTemplateForm.getName())) {
            errors.get("name", new ArrayList<>()).get().add("템플릿 이름을 공백으로 입력할 수 없습니다");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "template/entity/add";
        }

        SpreadSheetTable spreadSheetTable = entityTemplateForm.getSpreadSheetTable();
        Map<String, ColumnType> columnTypes = converter.batchGetMap(spreadSheetTable.getColumnValues(true));
        spreadSheetTable.getRowValues(0)
                .stream()
                .map(value -> new ColumnName(String.valueOf(value)))
                .iterator()
                .forEachRemaining(columnName -> columnUpdateForms.addForm(new ColumnUpdateForm(columnName, columnTypes.get(columnName.getValidName()))));

        entityTemplateForm.setColumnUpdateForms(columnUpdateForms);
        entityValidator.validate(entityTemplateForm, errors);
        model.addAttribute("errors", errors);
        return "template/entity/add";
    }

    @GetMapping("/save_template/{memberId}")
    public String saveTemplate(@PathVariable String memberId,
                               @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                               Model model) {

        Source<List<String>> errors = new Source<>();
        EntityTemplateForm templateForm = templateFormProvider.getEntityTemplateForm(memberId);
        entityValidator.validate(templateForm, errors);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "template/entity/add";
        }

        Entity entity = new Entity(templateForm);
        EntityDTO entityDTO;
        try {
            entityDTO = templateService.saveEntity(entity, member.getId());
        } catch (RuntimeException e) {
            throw new IllegalStateException("알 수 없는 오류로 서비스가 중단되었습니다");
        }

        templateFormProvider.clear(memberId);
        String url = "/template/" + memberId + "/" + entityDTO.getId();
        return "redirect:" + url;
    }

    @GetMapping("/update_template/{memberId}")
    public String updateTemplate(@SessionAttribute(name = SessionConst.TEMPLATE_ID, required = false) Long templateId,
                                 @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                                 @PathVariable String memberId, Model model) {

        Source<List<String>> errors = new Source<>();
        EntityTemplateForm templateForm = templateFormProvider.getEntityTemplateForm(memberId);
        BindingResult bindingResult = new BeanPropertyBindingResult(templateForm, "columnUpdateForm");
        entityValidator.validate(templateForm, errors);

        if (!errors.isEmpty()) {
            log.info("bindingResult={}", bindingResult);
            bindingResult.reject("invalid.template");
            return "template/entity/add";
        }

        Entity entity = new Entity(templateForm);
        templateService.deleteTemplate(templateId);
        EntityDTO entitySaveDTO = templateService.saveEntity(entity, member.getId());
        templateFormProvider.clear(memberId);
        log.info("model={}", model);
        return "/template/template";
    }

    @PostMapping("/edit_column/{memberId}")
    public String editColumn(@ModelAttribute("columnUpdateForm") ColumnUpdateForm columnUpdateForm, BindingResult bindingResult,
                             @PathVariable String memberId, Model model) {

        Source<List<String>> errors = new Source<>();
        EntityTemplateForm templateForm = templateFormProvider.getEntityTemplateForm(memberId);
        ColumnUpdateForm column = templateForm.getColumnUpdateForms().getColumnUpdateForm(columnUpdateForm.getName()).get();
        column.setType(columnUpdateForm.getType());

        entityValidator.validate(templateForm, errors);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            log.info("bindingResult={}", bindingResult);
            log.info("errors={}", errors);
            return "/template/entity/add::#edit-column";
        }
        return "/template/entity/add::#edit-column";
    }

    @PostMapping("/join_template/{memberId}")
    public String joinTemplate(@ModelAttribute("joinConditionSaveForm") JoinConditionSaveForm conditionSaveForm,
                               @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                               @PathVariable String memberId, BindingResult bindingResult, Model model) {

        Source<List<String>> errors = new Source<>();
        String referenceTarget = conditionSaveForm.getTemplateName();
        String columnName = new ColumnName(conditionSaveForm.getColumnName()).getValidName();
        EntityTemplateForm templateForm = templateFormProvider.getEntityTemplateForm(memberId);
        ColumnUpdateForm columnUpdateForm = templateForm.getColumnUpdateForms().getColumnUpdateForm(columnName).get();

        Entity entity = (Entity) templateService.findEntitiesByName(referenceTarget).get(0);
        SpreadSheetSource spreadSheetSource = new SpreadSheetSource(member.getFileId());
        DataSourceId dataSourceId = (DataSourceId) spreadSheetSource.getDataSourceIds(new Source(Arrays.asList(FILE_NAME), Arrays.asList(entity.getSheetTitle()))).get(0);

        SpreadSheetTable spreadSheetTable = templateForm.getSpreadSheetTable();
        SpreadSheetTable joinSpreadSheetTable;
        SpreadSheetSource joinSpreadSheetSource = new SpreadSheetSource(member.getFileId());

        entity.setSourceId(dataSourceId.getOriginalFileId());
        templateForm.addJoinEntity(entity);

        try {
            joinSpreadSheetTable = joinSpreadSheetSource.getSpreadSheetTable(new Source<>(Arrays.asList(FILE_NAME, FILE_RANGE), Arrays.asList(entity.getSheetTitle(), entity.getSheetRange())))
                    .get(SpreadSheetSource.SPREAD_SHEET_VALUES)
                    .orElseThrow(IllegalStateException::new);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        for (Column column : templateService.findColumnsByTemplateId(entity.getId())) {
            entity.getColumns().add(column);
        }

        Column primaryColumn = entity.getPrimaryKeyColumn().orElse(null);

        // PK가 존재하지 않는 경우
        if (primaryColumn == null) {
            String message = " PK가 존재하지 않는 템플릿 {1}을 칼럼 {0}에 조인할 수 없습니다. ";
            message = MessageConverterUtils.convertMessage(message, new Object[]{referenceTarget, columnName});
            errors.get("joinCondition", new ArrayList<>()).get().add(message);
//            bindingResult.reject(CONDITION_ERROR_CODE, new Object[]{columnName, ValueConditionType.FOREIGN_KEY, templateForm.getSpreadSheetTable().getColumnRange(columnName)}, "조인할 템플릿에 키 칼럼이 존재하지 않습니다");
        }

        // PK가 존재
        else {
            // 조인 칼럼 타입 불일치
            if (converter.getType(spreadSheetTable.getColumnValues(columnName, false)) != ColumnType.valueOf(primaryColumn.getType())) {
                String message = " 템플릿 {1}의 PK 칼럼 타입이 맞지 않아 칼럼 {0}에 조인할 수 없습니다.";
                message = MessageConverterUtils.convertMessage(message, new Object[]{referenceTarget, columnName});
                errors.get("joinCondition", new ArrayList<>()).get().add(message);
//              bindingResult.reject(TYPE_ERROR_CODE, new Object[]{columnName, ValueConditionType.FOREIGN_KEY, spreadSheetTable.getColumnRange(columnName)}, );
            }
            // 참조 값 무결성 위반
            for (Object value : spreadSheetTable.getColumnValues(columnName, false)) {
                if (!joinSpreadSheetTable.getColumnValues(primaryColumn).contains(value)) {
                    String message = " 템플릿 {1}의 PK 칼럼에 존재하지 않는 값이 칼럼 {0}에 포함되어 있어 참조 무결성에 위배됩니다";
                    message = MessageConverterUtils.convertMessage(message, new Object[]{referenceTarget, columnName});
                    errors.get("joinCondition", new ArrayList<>()).get().add(message);
                    break;
//                  bindingResult.reject(CONDITION_ERROR_CODE, new Object[]{columnName, ValueConditionType.FOREIGN_KEY, templateForm.getSpreadSheetTable().getColumnRange(columnName)}, "존재하지 않는 외부키를 갖고 있습니다");
                }
            }
        }

        entityValidator.validate(templateForm, errors);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "/template/entity/add::#edit-column";
        }

        entity.setSourceId(dataSourceId.getOriginalFileId());
        templateForm.addJoinEntity(entity);

        String updateMode = conditionSaveForm.getUpdateMode() != null ? conditionSaveForm.getUpdateMode() : OptionType.NO_ACTION.name();
        String deleteMode = conditionSaveForm.getDeleteMode() != null ? conditionSaveForm.getDeleteMode() : OptionType.NO_ACTION.name();
        String referenceColumn = primaryColumn.getColumnName().getValidName();
        conditionSaveForm.setJoinColumn(referenceColumn);

        ForeignKey foreignKey = new ForeignKey(referenceTarget, referenceColumn, OptionType.valueOf(updateMode), OptionType.valueOf(deleteMode));
        columnUpdateForm.addValueCondition(foreignKey);
        columnUpdateForm.setForeignKey(foreignKey);
        return "/template/entity/add::#edit-column";
    }


    @PostMapping("/add_condition/{memberId}")
    public String addCondition(@ModelAttribute("conditionSaveForm") ConditionSaveForm conditionSaveForm, BindingResult bindingResult,
                               @PathVariable String memberId, Model model) {

        EntityTemplateForm templateForm = templateFormProvider.getEntityTemplateForm(memberId);
        ColumnUpdateForm column = templateForm.getColumnUpdateForms().getColumnUpdateForm(conditionSaveForm.getColumnName()).get();
        Source<List<String>> errors = new Source<>();

        if (conditionSaveForm.isValidKeyCondition()) {
            KeyCondition target = column.getKeyConditions().stream()
                    .filter(keyCondition -> keyCondition.getConditionType()
                            .match(conditionSaveForm.getKeyConditionType())).findAny().orElse(null);
            if (target != null) {
                errors.get("keyConditionType", new ArrayList<>()).get().add("동일한 key 조건이 존재합니다");
            } else {
                column.addKeyCondition(new KeyCondition(conditionSaveForm.getKeyConditionType()));
            }
        } else {
            ValueCondition target = column.getValueConditions().stream()
                    .filter(valCondition -> valCondition.getConditionType()
                            .match(conditionSaveForm.getValueConditionType())).findAny().orElse(null);
            if (target != null) {
                errors.get("valueConditionType", new ArrayList<>()).get().add("동일한 value 조건이 존재합니다");
            } else {
                column.addValueCondition(new ValueCondition(conditionSaveForm.getValueConditionType(), conditionSaveForm.getValueConditionTerm()));
            }
        }

        entityValidator.validate(templateForm, errors);

        if (!errors.isEmpty()) {
//            if (conditionSaveForm.isValidKeyCondition()) {
//                column.deleteKeyCondition(new KeyCondition(conditionSaveForm.getKeyConditionType()));
//            } else {
//                column.deleteValueCondition(new ValueCondition(conditionSaveForm.getValueConditionType(), conditionSaveForm.getValueConditionTerm()));
//            }
            model.addAttribute("errors", errors);
            return "/template/entity/add::#edit-column";
        }

        return "/template/entity/add::#edit-column";
    }

    @PostMapping("/delete_condition/{memberId}")
    public String deleteCondition(@ModelAttribute("conditionSaveForm") ConditionSaveForm conditionSaveForm, BindingResult bindingResult,
                                  @PathVariable String memberId, Model model) {

        Source<List<String>> errors = new Source<>();
        EntityTemplateForm templateForm = templateFormProvider.getEntityTemplateForm(memberId);
        ColumnUpdateForm column = templateForm.getColumnUpdateForms().getColumnUpdateForm(conditionSaveForm.getColumnName()).get();

        if (conditionSaveForm.isValidKeyCondition()) {
            KeyCondition target = column.getKeyConditions()
                    .stream()
                    .filter(keyCondition -> keyCondition.getConditionType().match(conditionSaveForm.getKeyConditionType()))
                    .findAny().get();
            column.deleteKeyCondition(target);
        } else {
            ValueCondition target = column.getValueConditions()
                    .stream()
                    .filter(valCondition -> valCondition.getConditionType().match(conditionSaveForm.getValueConditionType()))
                    .findAny().get();
            column.deleteValueCondition(target);
        }

        entityValidator.validate(templateForm, errors);
        model.addAttribute("errors", errors);
        return "/template/entity/add::#edit-column";
    }
}
