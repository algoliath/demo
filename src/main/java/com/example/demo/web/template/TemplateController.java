package com.example.demo.web.template;

import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.column.form.ColumnSaveForm;
import com.example.demo.domain.repository.template.ColumnStore;
import com.example.demo.domain.repository.template.TemplateStore;
import com.example.demo.domain.template.EntityTemplateForm;
import com.example.demo.domain.template.Template;
import com.example.demo.domain.template.TemplateForm;
import com.example.demo.domain.template.TemplateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/template")
@RequiredArgsConstructor
public class TemplateController {

      private final TemplateFormProvider templateFormProvider;
      private final TemplateStore templateStore;
      private final ColumnStore columnStore;

      @GetMapping("/add_template/{memberId}")
      public String addTemplateForm(@PathVariable String memberId, Model model){
            TemplateForm templateForm = getTemplateForm(memberId);
            model.addAttribute("memberId", memberId);
            model.addAttribute("columnSaveForm", new ColumnSaveForm());
            model.addAttribute("columnUpdateForm", new ColumnUpdateForm());
            model.addAttribute("template", templateForm);
            model.addAttribute("columns", templateForm.getColumns().values());
            return "template/templateMain";
      }

      @PostMapping("/add_template/{memberId}")
      public String addTemplate(@ModelAttribute TemplateForm templateForm, BindingResult result, @PathVariable String memberId,
                                @RequestParam Map<String, Object> paramMap, Model model){

            String templateName = templateForm.getName();
            String templateType = templateForm.getType();

            log.info("-----------[POST START]-------------");
            log.info("paramMap={}", paramMap);

            templateForm = getTemplateForm(memberId);
            templateForm.setName(templateName);
            templateForm.setType(templateType);

            if(TemplateType.match(templateType, TemplateType.ENTITY)){
                  model.addAttribute("template", new EntityTemplateForm(templateForm));
            }
            else{
                  model.addAttribute("template", new EntityTemplateForm(templateForm));
            }

            model.addAttribute("columnSaveForm", new ColumnSaveForm());
            model.addAttribute("columnUpdateForm", new ColumnUpdateForm());
            model.addAttribute("columns", templateForm.getColumns().values());
            return "template/templateMain";
      }

      @GetMapping("/edit_template/{memberId}/{templateId}")
      public String editTemplateForm(@PathVariable String memberId, @PathVariable Long templateId, Model model){

            Template template = templateStore.findByTemplateId(templateId);
            String templateType = template.getType().toString();

            TemplateForm templateForm = getTemplateForm(memberId);
            templateForm.setName(template.getName());
            templateForm.setType(templateType);
            templateForm.setColumns(template.getColumns());

            model.addAttribute("columnSaveForm", new ColumnSaveForm());
            model.addAttribute("columnUpdateForm", new ColumnUpdateForm());
            model.addAttribute("template", templateForm);
            model.addAttribute("columns", templateForm.getColumns().values());
            return "template/templateMain";
      }

      @GetMapping("/{template_id}")
      public String template(@PathVariable Long template_id, Model model){
            Template template = templateStore.findByTemplateId(template_id);
            model.addAttribute("columns", template.getColumns().values());
            return "template/template";
      }

      @GetMapping("/save_template/{memberId}")
      public String saveTemplate(@PathVariable String memberId, Model model){
            TemplateForm templateForm = getTemplateForm(memberId);
            templateFormProvider.clear(memberId);
            log.info("templateForm={} 생성", templateForm);

            Template template = new Template(templateForm.getName(),
                    TemplateType.valueOf(templateForm.getType()),
                    templateForm.getColumns(), memberId);

            // create a new template with assigned template id
            templateStore.save(template);
            columnStore.saveBatch(template, templateForm.getColumns().values());
            model.addAttribute("columns", templateForm.getColumns().values());
            return "template/template";
      }

      private TemplateForm getTemplateForm(String memberId) {
            return templateFormProvider.getTemplateForm(memberId);
      }

}
