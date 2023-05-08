package com.example.demo.domain.validation;

import com.example.demo.domain.column.property.condition.key.KeyCondition;
import com.example.demo.domain.column.property.condition.key.KeyConditionType;
import com.example.demo.domain.column.property.condition.options.OptionType;
import com.example.demo.domain.column.property.condition.value.ForeignKey;
import com.example.demo.domain.column.property.condition.value.ValueCondition;
import com.example.demo.domain.column.property.condition.value.ValueConditionTerm;
import com.example.demo.domain.column.property.condition.value.ValueConditionType;
import com.example.demo.domain.column.form.ColumnUpdateForm;
import com.example.demo.domain.column.form.ColumnUpdateForms;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import com.example.demo.domain.source.datasource.SpreadSheetSource;
import com.example.demo.domain.data.vo.template.entity.EntityTemplateForm;
import com.example.demo.domain.template.type.TemplateType;
import com.example.demo.util.Source;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Transactional
@SpringBootTest
class EntityValidationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    @Qualifier("entityValidator")
    private EntityValidator entityValidator;
    private SpreadSheetSource spreadSheetSource;
    private SpreadSheetTable spreadSheetTable;

    @BeforeEach
    void setup(){
        Member member = memberRepository.findByLoginId("test").get();
        spreadSheetSource = new SpreadSheetSource(member.getFileId());
        String spreadSheetTitle = "Consultant";
        String spreadSheetRange = "Sheet1!A1:D5";
        Source<String> paramSource = new Source<>(Arrays.asList(SpreadSheetSource.FILE_NAME, SpreadSheetSource.FILE_RANGE), Arrays.asList(spreadSheetTitle, spreadSheetRange));
        try {
            spreadSheetTable = spreadSheetSource.getSpreadSheetTable(paramSource).get(SpreadSheetSource.SPREAD_SHEET_VALUES).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void no_error(){

        EntityTemplateForm templateForm = new EntityTemplateForm();
        ColumnUpdateForms columnUpdateForms = new ColumnUpdateForms();
        templateForm.setName("template4");
        templateForm.setType(TemplateType.ENTITY.name());

        ColumnUpdateForm columnUpdateForm1 = new ColumnUpdateForm("consultant_id", "NUMBER");
        columnUpdateForm1.addKeyCondition(new KeyCondition(KeyConditionType.PRIMARY_KEY));
        columnUpdateForm1.addValueCondition(new ForeignKey("template5", "id", OptionType.CASCADE, OptionType.CASCADE));

        ColumnUpdateForm columnUpdateForm2 = new ColumnUpdateForm("email", "CHARACTER");
        columnUpdateForm2.addKeyCondition(new KeyCondition(KeyConditionType.NOT_NULL));
        columnUpdateForm2.addValueCondition(new ValueCondition(ValueConditionType.LIKE, new ValueConditionTerm("com")));

        columnUpdateForms.addForm(columnUpdateForm1);
        columnUpdateForms.addForm(columnUpdateForm2);

        templateForm.setSpreadSheetTable(spreadSheetTable);
        templateForm.setColumnUpdateForms(columnUpdateForms);

        Source<List<String>> bindingResult = new Source<>();
        entityValidator.validate(templateForm, bindingResult);

        log.info("bindingResult={}", bindingResult);
        Assertions.assertThat(!bindingResult.isEmpty()).isTrue();
    }

}