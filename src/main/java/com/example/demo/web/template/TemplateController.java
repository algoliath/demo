package com.example.demo.web.template;

import com.example.demo.domain.form.ColumnUpdateForm;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import com.example.demo.domain.template.Column;
import com.example.demo.domain.template.ColumnName;
import com.example.demo.domain.form.ColumnSaveForm;
import com.example.demo.domain.service.TemplateCreateService;
import com.example.demo.domain.template.Template;
import com.example.demo.web.validation.ColumnSaveFormValidator;
import com.example.demo.web.validation.ColumnUpdateFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequestMapping("/template")
@RequiredArgsConstructor
public class TemplateController {

      private final TemplateCreateService service;
      private final MemberRepository memberRepository;
      private final ColumnSaveFormValidator saveFormValidator;
      private final ColumnUpdateFormValidator updateFormValidator;

      private Map<String, Column> columns = new ConcurrentHashMap<>();

      @GetMapping("/add_column/{memberId}")
      public String addColumnForm(@PathVariable Long memberId, Model model){
            model.addAttribute("memberId", memberId);
            log.info("-----------[GET START]-------------");
            model.addAttribute("column", new ColumnSaveForm());
            model.addAttribute("columns", columns.values());
            log.info("-----------[GET END]--------------\n");
            return "/template/addColumn";
      }

      @PostMapping("/add_column/{memberId}")
      public String addColumn(@PathVariable String memberId, @ModelAttribute("column") ColumnSaveForm saveForm, BindingResult bindingResult, Model model){

            log.info("-----------[POST START]-------------");
            log.info("[columns]={}", columns);
            log.info("[columnSaveForm]={}", saveForm);

            model.addAttribute("columns", columns.values());
            model.addAttribute("memberId", memberId);

            // 검증 로직 적용: 필드 에러
            saveFormValidator.validate(saveForm, bindingResult);
            ColumnName columnName = new ColumnName(saveForm.getName());

            // 검증 로직 적용: 복합 에러
            if(columns.containsKey(columnName.getName())){
                  bindingResult.reject("duplicate.column.add");
            }

            // 에러 검출
            if(bindingResult.hasErrors()){
                  log.info("errors={}", bindingResult);
                  log.info("-----------[POST END]-------------\n");
                  return "/template/addColumn";
            }

            Column column = new Column(columnName, saveForm.getType());
            columns.put(columnName.getName(), column);

            model.addAttribute("column", new ColumnSaveForm());
            model.addAttribute("columns", columns.values());
            log.info("[columns]={}", columns);
            log.info("-----------[POST END]-------------\n");
            return "/template/addColumn";

      }

      @PostMapping("/edit_column/{memberId}")
      public String editColumn(@PathVariable String memberId, @ModelAttribute("column") ColumnUpdateForm updateForm, BindingResult bindingResult, Model model){

            log.info("-----------[POST START]-------------");
            log.info("[columns]={}", columns);
            log.info("[columnUpdateForm]={}", updateForm);

            model.addAttribute("columns", columns.values());
            model.addAttribute("memberId", memberId);

            // 검증 로직 적용: 필드 에러
            updateFormValidator.validate(updateForm, bindingResult);
            ColumnName columnName = new ColumnName(updateForm.getName());

            // 에러 검출
            if(bindingResult.hasErrors()){
                  log.info("errors={}", bindingResult);
                  log.info("-----------[POST END]-------------\n");
                  return "/template/addColumn";
            }

            Column column = new Column(columnName, updateForm.getType());

            if(updateForm.getMode().equals("delete")){
                  columns.remove(columnName.getName());
            }
            else{
                  columns.remove(updateForm.getLastName());
                  columns.put(columnName.getName(), column);
            }

            model.addAttribute("column", new ColumnSaveForm());
            model.addAttribute("columns", columns.values());
            log.info("[columns]={}", columns);
            log.info("-----------[POST END]-------------\n");
            return "/template/addColumn";
      }


      @PostMapping("/add_template/{memberId}")
      public String addTemplateForm(@PathVariable("memberId") Long memberId, Model model){
            Member member = memberRepository.findById(memberId);
            log.info("member={}", member);
            model.addAttribute("memberId", memberId);
            return "template/addTemplate";
      }

      @GetMapping("/save_template/{memberId}")
      public String saveTemplate(@RequestParam("templateName") String templateName, @PathVariable Long memberId){
            Template template = new Template(memberId, templateName);
            service.createTemplate(template);
            return "template/template";
      }

}
