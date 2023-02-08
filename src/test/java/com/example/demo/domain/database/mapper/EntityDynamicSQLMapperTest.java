package com.example.demo.domain.database.mapper;

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
import com.example.demo.domain.template.form.EntityTemplateForm;
import com.example.demo.domain.template.model.Entity;
import com.example.demo.util.Source;
import groovy.util.logging.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;

import static com.example.demo.domain.source.datasource.SpreadSheetSource.*;

@Slf4j
@SpringBootTest
class EntityDynamicSQLMapperTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EntityDynamicSQLMapper entityDynamicSQLMapper;
    private EntityTemplateForm templateForm;
    private SpreadSheetSource spreadSheetSource;

    @BeforeEach
    void testcase1(){
        Member member = memberRepository.findByLoginId("test").get();
        spreadSheetSource = new SpreadSheetSource(member.getFileId());
        templateForm = new EntityTemplateForm();
        templateForm.setName("temp");
        templateForm.setType("ENTITY");

    }

    @Test
    void createTableQuery(){

        ColumnUpdateForms columnUpdateForms = templateForm.getColumnUpdateForms();
        ColumnUpdateForm columnUpdateForm1 = new ColumnUpdateForm("column1", "NUMBER");
        columnUpdateForm1.addKeyCondition(new KeyCondition(KeyConditionType.PRIMARY_KEY));
        columnUpdateForm1.addValueCondition(new ValueCondition(ValueConditionType.GREATER_THAN, new ValueConditionTerm("-1")));
        columnUpdateForm1.addValueCondition(new ValueCondition(ValueConditionType.LESS_THAN, new ValueConditionTerm("1000")));
        columnUpdateForm1.addValueCondition(new ForeignKey("template1", OptionType.NO_ACTION, OptionType.NO_ACTION));

        ColumnUpdateForm columnUpdateForm2 = new ColumnUpdateForm("column2", "CHARACTER");
        columnUpdateForm2.addKeyCondition(new KeyCondition(KeyConditionType.NOT_NULL));
        columnUpdateForm2.addValueCondition(new ValueCondition(ValueConditionType.LIKE, new ValueConditionTerm(".com")));

        columnUpdateForms.addForm(columnUpdateForm1);
        columnUpdateForms.addForm(columnUpdateForm2);
        Entity entity = new Entity(templateForm);

        try {
            SpreadSheetTable spreadSheetTable = spreadSheetSource.getSpreadSheetTable(new Source<>(Arrays.asList(FILE_NAME, FILE_RANGE), Arrays.asList("Consultant", "Sheet1!A1:D5")))
                    .get(SPREAD_SHEET_VALUES).get();
            entity.setSheetTable(spreadSheetTable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String query = "create table temp\n(" +
                "column1 integer,\n" +
                "column2 varchar not null,\n" +
                "primary key (column1),\n" +
                "foreign key (column1) references template1 on update cascade on delete cascade,\n"+
                "check(column1 > -1 AND column1 < 1000 AND column2 LIKE '.com')\n" +
                ");\n"+
                "INSERT INTO temp VALUES(1,'Brian','jungmu971@naver.com',1012341651);\n" +
                "INSERT INTO temp VALUES(2,'Brian','jungmu971@naver.com',1012341651);\n" +
                "INSERT INTO temp VALUES(3,'Patrick','jungmu971@naver.com',1012341651);\n" +
                "INSERT INTO temp VALUES(4,'Bob','jungmu971@naver.com',1012341651);";

        Assertions.assertThat(entityDynamicSQLMapper.createTableQuery(entity)).isEqualToIgnoringCase(query);
    }

    @Test
    void insertTableQuery(){

        ColumnUpdateForms columnUpdateForms = templateForm.getColumnUpdateForms();
        ColumnUpdateForm columnUpdateForm1 = new ColumnUpdateForm("column1", "NUMBER");
        columnUpdateForm1.addKeyCondition(new KeyCondition(KeyConditionType.PRIMARY_KEY));
        columnUpdateForm1.addValueCondition(new ValueCondition(ValueConditionType.GREATER_THAN, new ValueConditionTerm("-1")));
        columnUpdateForm1.addValueCondition(new ValueCondition(ValueConditionType.LESS_THAN, new ValueConditionTerm("1000")));
        columnUpdateForm1.addValueCondition(new ForeignKey("template1", OptionType.CASCADE, OptionType.CASCADE));

        ColumnUpdateForm columnUpdateForm2 = new ColumnUpdateForm("column2", "CHARACTER");
        columnUpdateForm2.addKeyCondition(new KeyCondition(KeyConditionType.NOT_NULL));
        columnUpdateForm2.addValueCondition(new ValueCondition(ValueConditionType.LIKE, new ValueConditionTerm(".com")));

        columnUpdateForms.addForm(columnUpdateForm1);
        columnUpdateForms.addForm(columnUpdateForm2);

        Entity entity = new Entity(templateForm);
        String query = "create table temp\n(" +
                "column1 integer,\n" +
                "column2 varchar not null,\n" +
                "primary key(column1),\n" +
                "check(column1 > -1 AND column1 < 1000 AND column2 LIKE '%.com')\n" +
                ");\n"+
                "INSERT INTO temp VALUES(1,'Brian','jungmu971@naver.com',1012341651);\n" +
                "INSERT INTO temp VALUES(2,'Brian','jungmu971@naver.com',1012341651);\n" +
                "INSERT INTO temp VALUES(3,'Patrick','jungmu971@naver.com',1012341651);\n" +
                "INSERT INTO temp VALUES(4,'Bob','jungmu971@naver.com',1012341651);";

        Assertions.assertThat(entityDynamicSQLMapper.createTableQuery(entity)).isEqualToIgnoringCase(query);
    }




}