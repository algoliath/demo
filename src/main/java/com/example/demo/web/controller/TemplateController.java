package com.example.demo.web.controller;
import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.column.form.ColumnSaveForm;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.data.dto.TemplateDTO;
import com.example.demo.domain.data.vo.template.entity.EntitySearchForm;
import com.example.demo.domain.data.vo.file.FileReadForm;
import com.example.demo.domain.data.vo.template.query.QueryBuilderForm;
import com.example.demo.domain.exception.ForeignKeyException;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import com.example.demo.domain.repository.template.mapper.TemplateMapper;
import com.example.demo.domain.source.datasource.DriveSource;
import com.example.demo.domain.source.datasource.SpreadSheetSource;
import com.example.demo.domain.source.datasource.wrapper.DataSourceId;
import com.example.demo.domain.source.datasource.wrapper.MimeTypes;
import com.example.demo.domain.data.vo.template.query.QueryTemplateForm;
import com.example.demo.domain.template.service.EntityTemplateService;
import com.example.demo.domain.template.service.TemplateService;
import com.example.demo.domain.data.vo.template.entity.EntityTemplateForm;
import com.example.demo.domain.data.vo.template.TemplateForm;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.model.Template;
import com.example.demo.domain.template.type.TemplateType;
import com.example.demo.util.Source;
import com.example.demo.util.template.TemplateFormProvider;
import com.example.demo.web.session.SessionConst;
import com.github.pagehelper.PageHelper;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.demo.domain.source.datasource.SpreadSheetSource.*;
import static com.example.demo.util.model.ModelUtils.sendForwardModelAttributes;
import static com.example.demo.util.model.ModelUtils.sendPostModelAttributes;
import static com.example.demo.util.datasource.CredentialUtils.*;
import static com.example.demo.util.datasource.SpreadSheetUtils.getEmbeddableSpreadSheetURL;

@Slf4j
@Controller
@RequestMapping("/template")
@RequiredArgsConstructor
public class TemplateController {

      private final MemberRepository memberRepository;
      private final TemplateService templateService;

      private final EntityTemplateService entityTemplateService;
      private final TemplateFormProvider templateFormProvider;
      private final TemplateMapper templateMapper;

      @GetMapping("/search_file/{memberId}")
      public String searchSideBarFiles(@ModelAttribute("searchForm") EntitySearchForm entitySearchForm,
                                       @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member,
                                       @PathVariable String memberId, Model model){
            String fileName = entitySearchForm.getName();
            String fileId = memberRepository.findById(member.getId()).get().getFileId();
            DriveSource drive = new DriveSource.Builder(MimeTypes.SHEETS)
                    .setCredentials(getCredentialPath(fileId))
                    .setFields("id, name")
                    .setPattern("contains")
                    .setTitle(fileName).build();
            List<File> files = drive.getSource();
            // 잘못된 객체 사용
            List<FileReadForm> fileReadForms = new ArrayList<>();
            for(File file: files){
                  fileReadForms.add(new FileReadForm(file.getId(), file.getName(), getEmbeddableSpreadSheetURL(file.getId())));
            }
            model.addAttribute("files", fileReadForms);
            return "/fragment/sidebar::#sidebar-file-section";
      }

      @GetMapping(value="/search_templates/sidebar/{memberId}", consumes="application/x-www-form-urlencoded;charset=UTF-8;")
      public String searchSideBarTemplates(@ModelAttribute EntitySearchForm entitySearchForm, @PathVariable String memberId,
                                           @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member, Model model) {
            TemplateForm templateForm = templateFormProvider.getTemplateForm(memberId);
            String templateName = entitySearchForm.getName();
            templateForm.setName(templateName);
            templateForm.setType(TemplateType.ENTITY.name());
            PageHelper.startPage(1, 1, false);

            List<Template> templates = templateService.findTemplatesByNameAndMemberId(templateName, member.getId());
            templates.remove(new Template(templateForm));
            model.addAttribute("templates", templates);
            model.addAttribute("entitySearchForm", entitySearchForm);
            log.info("templates={}", templates);
            return "fragment/sidebar::#sidebar-template-section";
      }


      @GetMapping(value = "/search_templates/mainframe/{memberId}", consumes = "application/x-www-form-urlencoded;charset=UTF-8;")
      public String searchMainFrameTemplates(@ModelAttribute EntitySearchForm entitySearchForm, @PathVariable String memberId,
                                             @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member, Model model) {
            TemplateForm templateForm = templateFormProvider.getTemplateForm(memberId);
            String templateName = entitySearchForm.getName();
            templateForm.setName(templateName);
            templateForm.setType(TemplateType.ENTITY.name());

            PageHelper.startPage(1, 5, false);
            List<Template> templates = templateService.findTemplatesByNameAndMemberId(templateName, member.getId());
            templates.remove(new Template(templateForm));
            model.addAttribute("templates", templates);
            model.addAttribute("entitySearchForm", entitySearchForm);
            log.info("templates={}", templates);
            return "template/entity/add::#mainframe";
      }

      @GetMapping("/add_template/{memberId}")
      public String addTemplateForm(@PathVariable String memberId, Model model){
            log.info("memberId={}", memberId);
            model.addAttribute("memberId", memberId);
            model.addAttribute("columnSaveForm", new ColumnSaveForm());
            model.addAttribute("columnUpdateForm", new ColumnUpdateForm());
            model.addAttribute("entitySearchForm", new EntitySearchForm());
            model.addAttribute("template", new TemplateForm());
            model.addAttribute("templateTypes", Arrays.stream(TemplateType.values()).toList());
            return "/template/layout/templateLayoutMain";
      }

      @PostMapping("/add_template/{memberId}")
      public String addTemplate(@Validated @ModelAttribute("template") TemplateForm templateForm, BindingResult bindingResult,
                                @PathVariable String memberId, Model model){

            String templateName = templateForm.getName();
            String templateType = templateForm.getType();
            model.addAttribute("templateTypes", Arrays.stream(TemplateType.values()).toList());
            model.addAttribute("entitySearchForm", new EntitySearchForm());
            log.info("-----------[POST START]-------------");
            log.info("memberId={}", memberId);
            log.info("templateForm={}", templateForm);

            List<TemplateDTO> identicalNames = templateMapper.findByName(templateName);
            // 이름 중복 방지 로직
            if(!identicalNames.isEmpty()){
                 bindingResult.rejectValue("name", "duplicate", new Object[]{templateName}, null);
            }
            if(bindingResult.hasErrors()){
                 log.info("bindingResult={}", bindingResult);
                 return "/template/layout/templateLayoutMain";
            }
            templateForm.setName(templateName);
            templateForm.setType(templateType);

            switch(TemplateType.valueOf(templateType)){
                  case ENTITY -> {
                        EntityTemplateForm entityTemplateForm = new EntityTemplateForm(templateForm);
                        templateFormProvider.setEntityTemplateForm(memberId, entityTemplateForm);
                        sendForwardModelAttributes(model, entityTemplateForm);
                        return "/template/entity/add";
                  }
                  case QUERY -> {
                        QueryTemplateForm queryTemplateForm = new QueryTemplateForm(templateForm);
                        queryTemplateForm.setQueryBuilderForm(new QueryBuilderForm());
                        log.info("queryTemplateForm={}", queryTemplateForm);
                        templateFormProvider.setQueryTemplateForm(memberId, queryTemplateForm);
                        sendForwardModelAttributes(model, queryTemplateForm);
                        return "/template/query/add";
                  }
            }
            return "/template/layout/templateLayoutMain";
      }

      @GetMapping("/edit_template/{memberId}/{templateId}")
      public String editTemplate(@PathVariable String memberId,
                                 @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member,
                                 @PathVariable Long templateId, Model model, HttpServletRequest request){

            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.TEMPLATE_ID, templateId);

            Member loginMember = memberRepository.findById(member.getId()).orElse(null);
            Entity entity = entityTemplateService.findEntityById(templateId);

            if(loginMember == null || entity == null){
                  throw new IllegalStateException(String.format("member=[%s], template=[%s]", loginMember, entity));
            }

            SpreadSheetSource spreadSheetSource = new SpreadSheetSource(loginMember.getFileId());

            Source<String> paramSource = new Source<>(Arrays.asList(FILE_NAME), Arrays.asList(entity.getSheetTitle()));
            paramSource.add(FILE_RANGE, entity.getSheetRange());
            DataSourceId dataSourceId = spreadSheetSource.getDataSourceId(paramSource);
            try {
                  SpreadSheetTable spreadSheetTable = spreadSheetSource.getSpreadSheetTable(paramSource).get(SPREAD_SHEET_VALUES).orElse(null);
                  entity.setSheetTable(spreadSheetTable);
            } catch (IOException e) {
                  throw new IllegalStateException("스프레드시트 범위 정보 에러" ,e);
            }

            entity.setSourceId(dataSourceId.getOriginalFileId());
            EntityTemplateForm templateForm = new EntityTemplateForm(entity);
            sendForwardModelAttributes(model, templateForm);
            sendPostModelAttributes(model, templateForm);
            templateFormProvider.setTemplateForm(memberId, templateForm);
            return "/template/entity/update";
      }

      @DeleteMapping("/delete/{memberId}/{templateId}")
      public String deleteTemplate(@PathVariable String memberId, @PathVariable Long templateId, Model model){
            try{
                  templateService.deleteTemplate(templateId);
            } catch(ForeignKeyException e){

            }
            return "redirect:/";
      }

      @GetMapping("/{memberId}/{templateId}")
      public String template(@PathVariable String memberId,
                             @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member member,
                             @PathVariable Long templateId, Model model) {

            Member loginMember = memberRepository.findById(member.getId()).orElse(null);
            Template template = templateService.findTemplateById(templateId).orElse(null);
            template.setTemplateType(TemplateType.ENTITY);

            if(loginMember == null || template == null){
                  throw new IllegalStateException(String.format("member=%s, template=%s", loginMember, template));
            }

            switch(template.getTemplateType()){
                  case ENTITY -> {
                        SpreadSheetSource spreadSheetSource = new SpreadSheetSource(loginMember.getFileId());
                        Entity entity = (Entity) template;
                        DataSourceId dataSourceId = (DataSourceId) spreadSheetSource.getDataSourceIds(new Source(Arrays.asList(FILE_NAME), Arrays.asList(entity.getSheetTitle()))).get(0);
                        entity.setColumns(templateService.findColumnsByTemplateId(templateId));
                        entity.setSourceId(dataSourceId.getOriginalFileId());

                        EntityTemplateForm templateForm = new EntityTemplateForm(entity);
                        templateFormProvider.setTemplateForm(memberId, templateForm);
                        model.addAttribute("entitySearchForm", new EntitySearchForm());
                        model.addAttribute("template", templateForm);
                        model.addAttribute("columns", templateForm.getColumnUpdateForms().getColumnUpdateFormList());
                  }
                  case QUERY -> {



                  }

            }
            return "template/template";
      }

}
