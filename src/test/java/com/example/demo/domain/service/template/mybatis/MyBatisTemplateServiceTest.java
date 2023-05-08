package com.example.demo.domain.service.template.mybatis;

import com.example.demo.domain.template.model.Template;
import com.example.demo.util.Source;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.column.form.ColumnUpdateForms;
import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.column.property.condition.key.KeyConditionType;
import com.example.demo.domain.column.property.condition.value.ValueCondition;
import com.example.demo.domain.column.property.condition.value.ValueConditionTerm;
import com.example.demo.domain.column.property.condition.value.ValueConditionType;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import com.example.demo.domain.data.vo.template.entity.EntityTemplateForm;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.service.TemplateService;
import com.example.demo.domain.template.type.TemplateType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional(readOnly=true)
@Slf4j
public class MyBatisTemplateServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TemplateService templateService;

    private Member member;

    @BeforeEach
    public void setup(){
        Member member = memberRepository.findByLoginId("test").get();
    }

    @Test()
    @Transactional
    public void save(){

        // create forms
        ColumnUpdateForm columnUpdateForm1 = new ColumnUpdateForm("id", "NUMBER");
        ColumnUpdateForm columnUpdateForm2 = new ColumnUpdateForm("name", "CHARACTER");
        ColumnUpdateForm columnUpdateForm3 = new ColumnUpdateForm("email", "CHARACTER");
        ColumnUpdateForm columnUpdateForm4 = new ColumnUpdateForm("phone_number", "NUMBER");

        // add conditions
        columnUpdateForm1.addKeyCondition(new KeyCondition(KeyConditionType.PRIMARY_KEY));
        columnUpdateForm3.addKeyCondition(new KeyCondition(KeyConditionType.NOT_NULL));
        columnUpdateForm3.addValueCondition(new ValueCondition(ValueConditionType.LIKE, new ValueConditionTerm("kr")));

        ColumnUpdateForms columnUpdateForms = createColumnUpdateForms(columnUpdateForm1, columnUpdateForm2, columnUpdateForm3, columnUpdateForm4);
        EntityTemplateForm templateForm = createTemplateForm(columnUpdateForms);

        Source<List<String>> errors = new Source<>();
        // attach spreadsheet files
        String spreadSheetTitle = "Student";
        String spreadSheetRange = "시트1!A1:D5";
        templateForm.attachFile(member.getFileId(), spreadSheetTitle, spreadSheetRange, errors);

        Entity entity = new Entity(templateForm);
        templateService.saveEntity(entity, 1L);

        Optional<Template> templateById = templateService.findTemplateById(entity.getId());
        Assertions.assertThat(templateById.isEmpty()).isTrue();
        Assertions.fail("엔티티가 저장되어 있습니다");
    }

    private static EntityTemplateForm createTemplateForm(ColumnUpdateForms columnUpdateForms) {
        EntityTemplateForm templateForm = new EntityTemplateForm();
        templateForm.setName("template1");
        templateForm.setType(TemplateType.ENTITY.name());
        templateForm.setColumnUpdateForms(columnUpdateForms);
        return templateForm;
    }

    private static ColumnUpdateForms createColumnUpdateForms(ColumnUpdateForm... columnUpdateFormList) {
        ColumnUpdateForms columnUpdateForms = new ColumnUpdateForms();
        for(ColumnUpdateForm columnUpdateForm: columnUpdateFormList){
            columnUpdateForms.addForm(columnUpdateForm);
        }
        return columnUpdateForms;
    }

}
