package com.example.demo.domain.datasource.source;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.column.ColumnType;
import com.example.demo.domain.column.ColumnTypeClassifier;
import com.example.demo.domain.datasource.Source;
import com.example.demo.domain.table.SpreadSheetTable;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class SpreadSheetsSourceTest {

    @Autowired
    private SpreadSheetsSource spreadSheetsSource;
    @Autowired
    private ColumnTypeClassifier classifier;

    private Source datasource;
    private List<List<Object>> tableElements;


    @TestConfiguration
    static class config{


    }

    @BeforeEach
    void setup(){
        String filename = "/auth/credentials.json";
        datasource = new Source();
        datasource.setData("credentials", filename);
        datasource.setData("spreadSheetTitle", "Consultant");
        datasource.setData("spreadSheetRange", "Sheet1!A1:B3");
    }

    @Test
    void getColumnTypes(){
        tableElements = spreadSheetsSource.getSource(datasource)
                .getData("spreadTableElements").orElse(new ArrayList<>());
        SpreadSheetTable table = new SpreadSheetTable(tableElements);
        String key1 = String.valueOf(tableElements.get(0).get(0));
        String key2 = String.valueOf(tableElements.get(0).get(1));
        log.info("[key1] = {}", key1);
        log.info("[key2] = {}", key2);

        ColumnType columnType1 = classifier.processBatch(table.getColumn(key1));
        ColumnType columnType2 = classifier.processBatch(table.getColumn(key2));
        assertThat(columnType1).isEqualTo(ColumnType.CHARACTER);
        assertThat(columnType2).isEqualTo(ColumnType.CHARACTER);
    }

    @Test
    void getSource() {
        tableElements = spreadSheetsSource.getSource(datasource)
                .getData("spreadTableElements").orElse(new ArrayList<>());
        log.info("[tableElements] = {}", tableElements);
    }

}