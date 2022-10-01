package com.example.demo.web.template;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.ColumnName;
import com.example.demo.domain.column.ColumnType;
import com.example.demo.domain.column.ColumnTypeClassifier;
import com.example.demo.domain.column.form.ColumnSaveForm;
import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.datasource.Source;
import com.example.demo.domain.datasource.source.SpreadSheetsSource;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberStore;
import com.example.demo.domain.table.SpreadSheetTable;
import com.example.demo.domain.template.EntityTemplateForm;
import com.example.demo.domain.template.TemplateForm;
import com.example.demo.web.validation.column.ColumnValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/template/entity")
@RequiredArgsConstructor
public class EntityController {

      private final TemplateFormProvider templateFormProvider;
      private final ColumnTypeClassifier classifier;
      private final ColumnValidator validator;
      private final MemberStore memberStore;

      @PostMapping("/add_entity/{memberId}")
      public String addTemplate(@RequestParam String spreadSheetTitle,
                                @RequestParam String spreadSheetRange,
                                @PathVariable String memberId, Model model){

            EntityTemplateForm templateForm = getEntityTemplateForm(memberId);
            model.addAttribute("columnSaveForm", new ColumnSaveForm());
            model.addAttribute("columns", templateForm.getColumns().values());

            Source datasource = new Source();
            Member findMember = memberStore.findById(memberId);
            datasource.setData("credentials", "/auth/" + findMember.getAttachFile().getOriginalFilename());
            datasource.setData("spreadSheetTitle", spreadSheetTitle);
            datasource.setData("spreadSheetRange", spreadSheetRange);

            Source<List<List<Object>>> source = spreadSheetsSource.getSource(datasource);
            SpreadSheetTable spreadSheetTable = new SpreadSheetTable(source);

            templateForm.setSpreadSheetRange(spreadSheetRange);
            templateForm.setSpreadSheetTitle(spreadSheetTitle);
            templateForm.setSpreadSheetTable(spreadSheetTable);

            model.addAttribute("template", templateForm);
            model.addAttribute("columnSaveForm", new ColumnSaveForm());
            model.addAttribute("columnUpdateForm", new ColumnUpdateForm());
            log.info("-----------[GET END]--------------\n");
            return "template/templateMain";
      }

      private final SpreadSheetsSource spreadSheetsSource;


      @PostMapping("/add_column/{memberId}")
      public String addColumn(@ModelAttribute("columnSaveForm") ColumnSaveForm saveForm, BindingResult bindingResult,
                              @PathVariable String memberId, Model model){

            EntityTemplateForm templateForm = getEntityTemplateForm(memberId);
            Map<String, Column> columns = templateForm.getColumns();

            log.info("-----------[POST START]-------------");
            log.info("[columns]={}", columns);
            log.info("[columnSaveForm]={}", saveForm);

            model.addAttribute("template", templateForm);
            model.addAttribute("columnSaveForm", saveForm);
            model.addAttribute("columns", columns.values());

            int i = 0;
            for(Column column: columns.values()){
                  model.addAttribute(i+"", column);
                  i++;
            }

            SpreadSheetTable table = templateForm.getSpreadSheetTable();
            String columnName = saveForm.getName();
            String columnType = saveForm.getType();
            String findColumnType = classifier.processBatch(table.getColumn(columnName)).name();

            // 칼럼 검증: 복합 에러
            if(!ColumnType.matchAny(columnType)){
                  if(!ColumnType.match(findColumnType, ColumnType.NULL)){
                        saveForm.setType(findColumnType);
                  }
                  bindingResult.reject("invalid.columnSaveForm.type");
            }

            if(columns.containsKey(saveForm.getName())){
                  bindingResult.reject("duplicate.column.add");
            }

            if(bindingResult.hasErrors()){
                  log.info("errors={}", bindingResult);
                  log.info("-----------[POST END]-------------\n");
                  return "template/templateMain";
            }

            Column column = new Column(columnName, columnType);
            columns.put(columnName, column);
            model.addAttribute("columnSaveForm", new ColumnSaveForm());
            model.addAttribute("columns", columns.values());

            log.info("[columns]={}", columns);
            log.info("-----------[POST END]-------------\n");
            return "template/templateMain";
      }

      @PostMapping("/edit_column/{memberId}")
      public String editColumn(@ModelAttribute("columnUpdateForm") ColumnUpdateForm updateForm,
                               @PathVariable String memberId, BindingResult bindingResult, Model model){

            EntityTemplateForm templateForm = getEntityTemplateForm(memberId);
            Map<String, Column> columns = templateForm.getColumns();

            log.info("-----------[POST START]-------------");
            log.info("[columns]={}", columns);
            log.info("[columnUpdateForm]={}", updateForm);

            model.addAttribute("template", templateForm);
            model.addAttribute("columnSaveForm", new ColumnSaveForm());
            model.addAttribute("columns", columns.values());

            int i = 0;
            for(Column column: columns.values()){
                  model.addAttribute("column" + i, column);
                  i++;
            }

            SpreadSheetTable table = templateForm.getSpreadSheetTable();
            String columnName = updateForm.getName();
            String columnType = updateForm.getType();
            String findColumnType = classifier.processBatch(table.getColumn(columnName)).name();

            // 검증 로직 적용: 복합 에러
            if(!ColumnType.matchAny(columnType)){
               if(!ColumnType.match(findColumnType, ColumnType.NULL)){
                     updateForm.setType(findColumnType);
               }
               bindingResult.reject("invalid.columnUpdateForm.type");
            }

            if(columns.containsKey(updateForm.getName())){
                  bindingResult.reject("duplicate.column.add");
            }

            if(bindingResult.hasErrors()){
                  log.info("errors={}", bindingResult);
                  log.info("-----------[POST END]-------------\n");
                  return "template/templateMain";
            }

            Column column = new Column(columnName, updateForm.getType());
            // 메인 로직
            if(updateForm.getMode().equals("delete")){
                  columns.remove(columnName);
            }
            else{
                  columns.remove(updateForm.getLastName());
                  columns.put(columnName, column);
            }

            model.addAttribute("columns", columns.values());
            log.info("[columns]={}", columns);
            log.info("-----------[POST END]-------------\n");
            return "template/templateMain";
      }

      private EntityTemplateForm getEntityTemplateForm(String memberId){
            return templateFormProvider.getEntityTemplateForm(memberId);
      }

}
