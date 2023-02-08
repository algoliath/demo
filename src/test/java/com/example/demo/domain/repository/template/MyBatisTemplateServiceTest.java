package com.example.demo.domain.repository.template;

import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.column.form.ColumnUpdateForms;
import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.column.property.condition.key.KeyConditionType;
import com.example.demo.domain.column.property.condition.options.OptionType;
import com.example.demo.domain.column.property.condition.value.ForeignKey;
import com.example.demo.domain.column.property.condition.value.ValueCondition;
import com.example.demo.domain.column.property.condition.value.ValueConditionTerm;
import com.example.demo.domain.column.property.condition.value.ValueConditionType;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import com.example.demo.domain.source.datasource.SpreadSheetSource;
import com.example.demo.domain.template.form.EntityTemplateForm;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.domain.template.service.TemplateService;
import com.example.demo.domain.template.type.TemplateType;
import com.example.demo.util.Source;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;

@SpringBootTest
@Slf4j
public class MyBatisTemplateServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TemplateService templateService;
    private SpreadSheetSource spreadSheetSource;
    private EntityTemplateForm templateForm;

    @BeforeEach
    public void setup(){
        Member member = memberRepository.findByLoginId("test").get();
        spreadSheetSource = new SpreadSheetSource(member.getFileId());

        String spreadSheetTitle = "Student";
        String spreadSheetRange = "시트1!A1:D5";
        templateForm = new EntityTemplateForm();
        templateForm.setSpreadSheetTitle(spreadSheetTitle);
        templateForm.setSpreadSheetRange(spreadSheetRange);

        Source<String> paramSource = new Source<>(Arrays.asList(SpreadSheetSource.FILE_NAME, SpreadSheetSource.FILE_RANGE), Arrays.asList(spreadSheetTitle, spreadSheetRange));
        try {
            SpreadSheetTable spreadSheetTable = spreadSheetSource.getSpreadSheetTable(paramSource).get(SpreadSheetSource.SPREAD_SHEET_VALUES).get();
            templateForm.setSpreadSheetTable(spreadSheetTable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
//    @Transactional
    public void templateService(){


        ColumnUpdateForms columnUpdateForms = new ColumnUpdateForms();
        templateForm.setName("template1");
        templateForm.setType(TemplateType.ENTITY.name());

        ColumnUpdateForm columnUpdateForm1 = new ColumnUpdateForm("id", "NUMBER");
        columnUpdateForm1.addKeyCondition(new KeyCondition(KeyConditionType.PRIMARY_KEY));

        ColumnUpdateForm columnUpdateForm2 = new ColumnUpdateForm("name", "CHARACTER");

        ColumnUpdateForm columnUpdateForm3 = new ColumnUpdateForm("email", "CHARACTER");
        columnUpdateForm3.addKeyCondition(new KeyCondition(KeyConditionType.NOT_NULL));
        columnUpdateForm3.addValueCondition(new ValueCondition(ValueConditionType.LIKE, new ValueConditionTerm("kr")));

        ColumnUpdateForm columnUpdateForm4 = new ColumnUpdateForm("phone_number", "NUMBER");

        columnUpdateForms.addForm(columnUpdateForm1);
        columnUpdateForms.addForm(columnUpdateForm2);
        columnUpdateForms.addForm(columnUpdateForm3);
        columnUpdateForms.addForm(columnUpdateForm4);
        templateForm.setColumnUpdateForms(columnUpdateForms);

        Entity entity = new Entity(templateForm);
        templateService.saveTemplate(entity, 1L);
    }

}
