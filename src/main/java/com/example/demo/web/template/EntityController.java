package com.example.demo.web.template;
import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.property.condition.options.OptionType;
import com.example.demo.domain.column.property.condition.value.ForeignKey;
import com.example.demo.domain.column.property.condition.value.ValueConditionType;
import com.example.demo.domain.column.property.name.ColumnName;
import com.example.demo.domain.data.vo.SearchForm;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import com.example.demo.domain.source.datasource.wrapper.DataSourceId;
import com.example.demo.domain.data.dto.EntityDTO;
import com.example.demo.domain.data.vo.EntityVO;
import com.example.demo.domain.template.form.TemplateForm;
import com.example.demo.domain.template.model.Template;
import com.example.demo.domain.template.type.TemplateType;
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
import com.example.demo.domain.template.form.EntityTemplateForm;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.util.template.TemplateFormProvider;
import com.example.demo.web.session.SessionConst;
import com.github.pagehelper.PageHelper;
import com.google.api.services.drive.model.File;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;
import static com.example.demo.domain.source.datasource.DataSource.*;
import static com.example.demo.util.controller.ControllerUtils.*;
import static com.example.demo.util.datasource.SpreadSheetUtils.getEmbeddableSpreadSheetURL;
import static com.example.demo.domain.validation.ErrorConst.*;

@Slf4j
@Controller
@RequestMapping("/template/entity")
@RequiredArgsConstructor
public class EntityController {

      private final MemberRepository memberRepository;
      private final TemplateService templateService;
      private final ColumnTypeConverter converter;
      private final TemplateFormProvider templateFormProvider;
      private final EntityValidator entityValidator;

//      @InitBinder
//      public void init(WebDataBinder webDataBinder){
//           log.info("webDataBinder={}", webDataBinder);
//           webDataBinder.addValidators(entityValidator);
//      }

      @GetMapping("/add_entity/{memberId}")
      public String searchFile(@ModelAttribute("searchForm") SearchForm searchForm,
                               @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member,
                               @PathVariable String memberId, Model model){

            String fileName = searchForm.getName();
            String fragment = searchForm.getFragment();
            saveEntityForwardModelAttributes(model, templateFormProvider.getEntityTemplateForm(memberId));
            String fileId = memberRepository.findById(member.getId()).get().getFileId();
            SpreadSheetSource spreadSheetSource = new SpreadSheetSource(fileId);

            Source<String> paramSource = new Source<>();
            paramSource.add(FILE_NAME, fileName);
            paramSource.add(PATTERN_MATCHER, "contains");

            List<File> files = spreadSheetSource.getFiles(paramSource);
            List<EntityVO> entityVOList = new ArrayList<>();
            for(File file: files){
                  entityVOList.add(new EntityVO(file.getId(), file.getName(), getEmbeddableSpreadSheetURL(file.getId())));
            }

            model.addAttribute("files", entityVOList);
            saveEntityPostModelAttributes(model, templateFormProvider.getEntityTemplateForm(memberId));
            return "/template/entity/add::#" + fragment;
      }

      /* Template Controller */
      @PostMapping("/add_entity/{memberId}")
      public String addFile(@ModelAttribute("searchForm") SearchForm searchForm, BindingResult bindingResult,
                            @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member,
                            @PathVariable String memberId, Model model){

            String credentials = memberRepository.findById(member.getId()).get().getFileId();
            String fileName = searchForm.getName();
            String frameId = searchForm.getFragment();

            SpreadSheetSource spreadSheetSource = new SpreadSheetSource(credentials);
            EntityTemplateForm entityTemplateForm = templateFormProvider.getEntityTemplateForm(memberId);

            entityTemplateForm.setSpreadSheetTitle(fileName);
            saveEntityForwardModelAttributes(model, entityTemplateForm);
            Source<String> paramSource = new Source<>(Arrays.asList(FILE_NAME), Arrays.asList(fileName));

            DataSourceId dataSourceId = spreadSheetSource.publish(paramSource, bindingResult);
            entityTemplateForm.setSourceId(dataSourceId.getOriginalFileId());
            saveEntityPostModelAttributes(model, entityTemplateForm);
            log.info("-----------[GET END]--------------\n");
            return "template/entity/add";
      }

      @PostMapping("/join_template/{memberId}")
      public String joinTemplate(@ModelAttribute("searchForm") SearchForm searchForm, BindingResult bindingResult,
                                 @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member,
                                 @PathVariable String memberId, Model model) {

            Source<List<String>> errors = new Source<>();
            EntityTemplateForm entityTemplateForm = templateFormProvider.getEntityTemplateForm(memberId);
            Member loginMember = memberRepository.findById(member.getId()).get();
            String fileId = loginMember.getFileId();
            String templateName = searchForm.getName();
            saveEntityForwardModelAttributes(model, entityTemplateForm);

            if(!errors.isEmpty()){
                  model.addAttribute("errors", errors);
                  return "/template/entity/add";
            }

            List<Template> templateResultSet = templateService.findTemplatesByName(templateName);
            Entity entity = (Entity) templateResultSet.get(0);
            entity.setColumns(templateService.findColumnsByTemplateId(entity.getId()));

            SpreadSheetSource spreadSheetSource = new SpreadSheetSource(fileId);
            spreadSheetSource = new SpreadSheetSource(loginMember.getFileId());
            DataSourceId dataSourceId = (DataSourceId) spreadSheetSource.getDataSourceIds(new Source(Arrays.asList(FILE_NAME), Arrays.asList(entity.getSheetTitle()))).get(0);

            entity.setSourceId(dataSourceId.getOriginalFileId());
            entityTemplateForm.getJoinTemplates().add(entity);
            saveEntityPostModelAttributes(model, templateFormProvider.getEntityTemplateForm(memberId));
            return "/template/entity/add";
      }


      @GetMapping(value="/search_entity/{memberId}", consumes="application/x-www-form-urlencoded;charset=UTF-8;")
      public String searchTemplate(@ModelAttribute SearchForm searchForm, @PathVariable String memberId, Model model) {
            TemplateForm templateForm = templateFormProvider.getTemplateForm(memberId);
            String templateName = searchForm.getName();
            String fragment = searchForm.getFragment();
            templateForm.setName(templateName);
            templateForm.setType(TemplateType.ENTITY.name());

            PageHelper.startPage(1, 1, false);
            List<Template> templates = templateService.findTemplatesByName(templateName);
            templates.remove(new Template(templateForm));
            model.addAttribute("templates", templates);
            log.info("templates={}", templates);
            log.info("fragment={}", fragment);
            return "template/entity/add::#" + fragment;
      }

      @PostMapping("/edit_template/{memberId}")
      public String editTemplate(@ModelAttribute("template") EntityTemplateForm entityTemplateForm, BindingResult bindingResult,
                                 @PathVariable String memberId, Model model){
            EntityTemplateForm templateForm = templateFormProvider.getEntityTemplateForm(memberId);
            templateForm.setSpreadSheetTitle(entityTemplateForm.getSpreadSheetTitle());
            saveEntityForwardModelAttributes(model, templateForm);
            model.addAttribute("template", templateForm);
            model.addAttribute("columns", templateForm.getColumnUpdateForms().getColumnUpdateFormList());
            saveEntityPostModelAttributes(model, templateForm);
            return "template/entitiy/update";
      }

      @PostMapping("/sync_entity/{memberId}")
      public String syncTemplate(@ModelAttribute("template") EntityTemplateForm templateForm, BindingResult bindingResult,
                                 @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member,
                                 @PathVariable String memberId, Model model){

            Source<List<String>> errors = new Source<>();
            String fileId = memberRepository.findById(member.getId()).get().getFileId();
            SpreadSheetSource spreadSheetSource = new SpreadSheetSource(fileId);
            EntityTemplateForm entityTemplateForm = templateFormProvider.getEntityTemplateForm(memberId);
            String spreadSheetTitle = templateForm.getSpreadSheetTitle();
            String spreadSheetRange = templateForm.getSpreadSheetRange();

            entityTemplateForm.setSpreadSheetTitle(spreadSheetTitle);
            entityTemplateForm.setSpreadSheetRange(spreadSheetRange);

            ColumnUpdateForms columnUpdateForms = new ColumnUpdateForms();
            saveEntityForwardModelAttributes(model, entityTemplateForm);
            Source<String> paramSource = new Source<>(Arrays.asList(FILE_NAME, FILE_RANGE), Arrays.asList(spreadSheetTitle, spreadSheetRange));
            SpreadSheetTable spreadSheetTable = null;

            try {
                  spreadSheetTable = spreadSheetSource.getSpreadSheetTable(paramSource).get("values").orElse(null);
                  if(spreadSheetTable == null){
                        throw new IllegalStateException();
                  }
            } catch (IOException e) {
                  errors.get("spreadSheetRange", new ArrayList<>()).get().add("템플릿 스프레드시트 범위가 유효하지 않습니다");
            } catch (IllegalStateException e) {
                  errors.get("spreadSheetRange", new ArrayList<>()).get().add("스프레드시트 파일에 값이 존재하지 않습니다");
            }

            if(!errors.isEmpty()){
                  model.addAttribute("errors", errors);
                  return "template/entity/add";
            }

            Map<String, ColumnType> validTypes = converter.batchGetMap(spreadSheetTable.getColumns(true));
            spreadSheetTable.getRow(0)
                    .stream()
                    .map(value -> new ColumnName(String.valueOf(value)))
                    .iterator()
                    .forEachRemaining(columnName -> columnUpdateForms.addForm(new ColumnUpdateForm(columnName, validTypes.get(columnName.getValidName()))));

            entityTemplateForm.setSpreadSheetTable(spreadSheetTable);
            entityTemplateForm.setColumnUpdateForms(columnUpdateForms);
            entityValidator.validate(entityTemplateForm, errors);
            templateFormProvider.setTemplateForm(memberId, entityTemplateForm);
            saveEntityPostModelAttributes(model, entityTemplateForm);
            model.addAttribute("errors", errors);
            return "template/entity/add";
      }

      @GetMapping("/save_template/{memberId}")
      public String saveTemplate(@ModelAttribute("templateForm") TemplateForm template, BindingResult bindingResult,
                                 @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member,
                                 RedirectAttributes redirectAttributes,
                                 @PathVariable String memberId, Model model){

            Source<List<String>> errors = new Source<>();
            EntityTemplateForm templateForm = templateFormProvider.getEntityTemplateForm(memberId);
            entityValidator.validate(templateForm, errors);
            saveEntityForwardModelAttributes(model, templateForm);

            if(!errors.isEmpty()){
                  model.addAttribute("errors", errors);
                  return "template/entity/add";
            }

            Entity entity = new Entity(templateForm);
            EntityDTO entityDTO = templateService.saveTemplate(entity, member.getId());
            templateFormProvider.clear(memberId);
            saveEntityForwardModelAttributes(redirectAttributes, templateForm);
            saveEntityPostModelAttributes(redirectAttributes, templateForm);
            return "redirect:/template/template/" + memberId;
      }

      @GetMapping("/update_template/{memberId}")
      public String updateTemplate(@SessionAttribute(name=SessionConst.TEMPLATE_ID, required = false) Long templateId,
                                   @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member,
                                   @PathVariable String memberId, Model model){

            Source<List<String>> errors = new Source<>();
            EntityTemplateForm templateForm = templateFormProvider.getEntityTemplateForm(memberId);
            BindingResult bindingResult = new BeanPropertyBindingResult(templateForm, "columnUpdateForm");
            entityValidator.validate(templateForm, errors);
            saveEntityForwardModelAttributes(model, templateForm);

            if(!errors.isEmpty()){
                  log.info("bindingResult={}", bindingResult);
                  bindingResult.reject("invalid.template");
                  return "template/entity/add";
            }

            Entity entity = new Entity(templateForm);
            templateService.deleteTemplate(templateId);
            EntityDTO entitySaveDTO = templateService.saveTemplate(entity, member.getId());
            saveEntityPostModelAttributes(model, templateForm);
            templateFormProvider.clear(memberId);
            log.info("model={}", model);
            return "/template/template";
      }

      @PostMapping("/edit_column/{memberId}")
      public String editColumn(@ModelAttribute("columnUpdateForm") ColumnUpdateForm columnUpdateForm, BindingResult bindingResult,
                               @PathVariable String memberId, Model model){

            Source<List<String>> errors = new Source<>();
            EntityTemplateForm templateForm = templateFormProvider.getEntityTemplateForm(memberId);
            saveEntityForwardModelAttributes(model, templateForm);
            ColumnUpdateForm column = templateForm.getColumnUpdateForms().getColumnUpdateForm(columnUpdateForm.getName()).get();
            column.setType(columnUpdateForm.getType());

            // 실제 테이블 값 기준으로 타입 검증
            entityValidator.validate(templateForm, errors);
            if(!errors.isEmpty()){
                  model.addAttribute("errors", errors);
                  log.info("bindingResult={}", bindingResult);
                  log.info("errors={}", errors);
                  return "/fragment/form::#edit-column";
            }

            saveEntityPostModelAttributes(model, templateForm);
            return "/fragment/form::#edit-column";
      }

      @PostMapping("/join_column/{memberId}")
      public String joinColumn(@ModelAttribute("joinConditionSaveForm") JoinConditionSaveForm conditionSaveForm,
                               @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member,
                               @PathVariable String memberId, BindingResult bindingResult, Model model){

            Member loginMember = memberRepository.findById(member.getId()).get();
            EntityTemplateForm templateForm = templateFormProvider.getEntityTemplateForm(memberId);
            String templateName = conditionSaveForm.getTemplateName();
            String columnName = new ColumnName(conditionSaveForm.getColumnName()).getValidName();
            ColumnUpdateForm columnUpdateForm = templateForm.getColumnUpdateForms().getColumnUpdateForm(columnName).get();
            Entity entity = (Entity) templateService.findTemplatesByName(templateName).get(0);
            SpreadSheetTable spreadSheetTable = templateForm.getSpreadSheetTable();
            SpreadSheetTable joinSpreadSheetTable;
            SpreadSheetSource joinSpreadSheetSource = new SpreadSheetSource(loginMember.getFileId());
            Source<List<String>> errors = new Source<>();

            try {
                  joinSpreadSheetTable = joinSpreadSheetSource.getSpreadSheetTable(new Source<>(Arrays.asList(FILE_NAME, FILE_RANGE), Arrays.asList(entity.getSheetTitle(), entity.getSheetRange()))).get(SpreadSheetSource.SPREAD_SHEET_VALUES).get();
                  if(spreadSheetTable == null){
                        throw new IllegalStateException();
                  }
            }
            catch (IOException e) {
                  throw new IllegalStateException(e);
            }

            for(Column column: templateService.findColumnsByTemplateId(entity.getId())){
                  entity.getColumns().add(column);
            }

            Column primaryColumn = entity.getPrimaryKeyColumn().orElse(null);

            // PK가 존재하지 않는 경우
            if(primaryColumn == null){
                  bindingResult.reject(CONDITION_ERROR_CODE, new Object[]{columnName, ValueConditionType.FOREIGN_KEY, templateForm.getSpreadSheetTable().getColumnRange(columnName)}, "조인할 템플릿에 키 칼럼이 존재하지 않습니다");
            }

            // 조인 칼럼 타입 불일치
            if(converter.getType(spreadSheetTable.getColumn(columnName, false)) != ColumnType.valueOf(primaryColumn.getType())){
                  bindingResult.reject(TYPE_ERROR_CODE, new Object[]{columnName, ValueConditionType.FOREIGN_KEY, spreadSheetTable.getColumnRange(columnName)}, "조인할 템플릿의 키 칼럼과 해당 칼럼의 타입이 일치하지 않습니다");
            }

            // 참조 값 무결성 위반
            for(Object value: spreadSheetTable.getColumn(columnName, false)){
                  if(!joinSpreadSheetTable.getColumn(primaryColumn).contains(value)){
                        bindingResult.reject(CONDITION_ERROR_CODE, new Object[]{columnName, ValueConditionType.FOREIGN_KEY, templateForm.getSpreadSheetTable().getColumnRange(columnName)}, "존재하지 않는 외부키를 갖고 있습니다");
                  }
            }

            saveEntityForwardModelAttributes(model, templateForm);
            if(!errors.isEmpty()){
                  model.addAttribute("errors", errors);
                  return "fragment/form::#edit-column";
            }

            String updateMode = conditionSaveForm.getUpdateMode();
            String deleteMode = conditionSaveForm.getDeleteMode();
            conditionSaveForm.setJoinColumn(primaryColumn.getColumnName().getValidName());

            if(conditionSaveForm.isValid()){
                columnUpdateForm.addValueCondition(new ForeignKey(templateName, OptionType.valueOf(updateMode), OptionType.valueOf(deleteMode)));
                saveEntityPostModelAttributes(model, templateForm);
            }

            model.addAttribute("joinConditionSaveForm", conditionSaveForm);
            return "fragment/form::#edit-column";
      }

      @PostMapping("/add_condition/{memberId}")
      public String addCondition(@ModelAttribute("conditionSaveForm") ConditionSaveForm conditionSaveForm, BindingResult bindingResult,
                                 @PathVariable String memberId, Model model){

            EntityTemplateForm templateForm = templateFormProvider.getEntityTemplateForm(memberId);
            ColumnUpdateForm column = templateForm.getColumnUpdateForms().getColumnUpdateForm(conditionSaveForm.getColumnName()).get();
            saveEntityForwardModelAttributes(model, templateForm);
            Source<List<String>> errors = new Source<>();

            if(conditionSaveForm.isValidKeyCondition()) {
                  KeyCondition target = column.getKeyConditions().stream()
                          .filter(keyCondition -> keyCondition.getConditionType()
                                  .match(conditionSaveForm.getKeyConditionType())).findAny().orElse(null);
                  if (target == null) {
                        column.addKeyCondition(new KeyCondition(conditionSaveForm.getKeyConditionType()));
                  } else {
                        bindingResult.rejectValue("keyConditionType", "duplicate", null);
                  }
            }
            else {
                  ValueCondition target = column.getValConditions().stream()
                          .filter(valCondition->valCondition.getConditionType()
                                  .match(conditionSaveForm.getValueConditionType())).findAny().orElse(null);
                  if(target == null){
                        column.addValueCondition(new ValueCondition(conditionSaveForm.getValueConditionType(), conditionSaveForm.getValueConditionTerm()));
                  } else{
                        bindingResult.rejectValue("valueConditionType", "duplicate", null);
                  }
            }

            if(!errors.isEmpty()) {
                  model.addAttribute("errors", errors);
                  return "/fragment/form::#edit-column";
            }

            saveEntityPostModelAttributes(model, templateForm);
            return "/fragment/form::#edit-column";
      }

      @PostMapping("/delete_condition/{memberId}")
      public String deleteCondition(@ModelAttribute("conditionSaveForm") ConditionSaveForm conditionSaveForm, BindingResult bindingResult,
                                    @PathVariable String memberId, Model model){

            Source<List<String>> errors = new Source<>();
            EntityTemplateForm templateForm = templateFormProvider.getEntityTemplateForm(memberId);
            ColumnUpdateForm column = templateForm.getColumnUpdateForms().getColumnUpdateForm(conditionSaveForm.getColumnName()).get();
            saveEntityForwardModelAttributes(model, templateForm);

            if(conditionSaveForm.isValidKeyCondition()){
                  KeyCondition target = column.getKeyConditions().stream().filter(keyCondition -> keyCondition.getConditionType().match(conditionSaveForm.getKeyConditionType()))
                          .findAny().get();
                  column.deleteKeyCondition(target);
            }
            else {
                  ValueCondition target = column.getValConditions().stream().filter(valCondition->valCondition.getConditionType().match(conditionSaveForm.getValueConditionType()))
                          .findAny().get();
                  column.deleteValueCondition(target);
            }

            // 타입 검증 + 조건 검증
            entityValidator.validate(templateForm, errors);
            model.addAttribute("errors", errors);
            saveEntityPostModelAttributes(model, templateForm);
            return "/fragment/form::#edit-column";
      }
}


//      @ResponseBody
//      @GetMapping(value="/search_entity/list/{memberId}", consumes="application/x-www-form-urlencoded;charset=UTF-8;")
//      public PageInfo<Template> searchTemplates(@ModelAttribute FileSearchForm fileSearchForm, @PathVariable Long memberId, Model model) {
//
//            String templateName = fileSearchForm.getFileName();
//            String frameId = fileSearchForm.getTableName();
//            List<Template> templates = new ArrayList<>();
//            PageHelper.startPage(1, 1, false);
//            if (StringUtils.hasText(templateName)) {
//                  templates = templateService.findTemplatesByName(templateName);
//                  model.addAttribute("templates", templates);
//                  log.info("templates={}", templates);
//            }
//            return new PageInfo(templates, 1);
//      }

