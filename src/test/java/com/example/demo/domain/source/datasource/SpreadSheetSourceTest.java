package com.example.demo.domain.source.datasource;

import com.example.demo.domain.column.property.type.ColumnType;
import com.example.demo.domain.column.ColumnTypeConverter;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.repository.member.MemberRepository;
import com.example.demo.util.Source;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.model.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class SpreadSheetSourceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ColumnTypeConverter columnTypeConverter;

    private SpreadSheetSource spreadSheetSource;
    private Source paramSource;
    private Member member;
    private List<List<Object>> tableElements;

    @TestConfiguration
    static class config{


    }

    @BeforeEach
    void setup(){
        member = memberRepository.findByLoginId("test").get();
        spreadSheetSource = new SpreadSheetSource(member.getFileId());
        paramSource = new Source();
        paramSource.add("spreadSheetTitle", "Consul");
        paramSource.add("matchPattern", "contains");
        paramSource.add("spreadSheetRange", "Sheet1!A1:B3");
    }

    @Test
    void getSourceIds(){
        List<File> files = spreadSheetSource.getFiles(paramSource);
        log.info("files={}", files);
        Assertions.assertEquals(files.size(), 1);
    }

    @Test
    void getColumnTypes(){
        try {
            tableElements = (List<List<Object>>) spreadSheetSource.getSpreadSheetTable(paramSource).get("spreadTableElements").get();
        } catch (GoogleJsonResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SpreadSheetTable table = new SpreadSheetTable((String) paramSource.get("spreadSheetRange").get(), tableElements);
        String key1 = String.valueOf(tableElements.get(0).get(0));
        String key2 = String.valueOf(tableElements.get(0).get(1));
        ColumnType columnType1 = columnTypeConverter.getType(table.getColumn(key1, false));
        ColumnType columnType2 = columnTypeConverter.getType(table.getColumn(key2, false));
        assertThat(columnType1).isEqualTo(ColumnType.CHARACTER);
        assertThat(columnType2).isEqualTo(ColumnType.CHARACTER);
    }

    @Test
    void getSource() {
        try {
            tableElements = (List<List<Object>>) spreadSheetSource
                    .getSpreadSheetTable(paramSource)
                    .get("spreadTableElements").orElse(new ArrayList<>());
        } catch (GoogleJsonResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}