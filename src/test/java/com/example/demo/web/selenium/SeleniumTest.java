package com.example.demo.web.selenium;

import com.example.demo.util.Source;
import com.example.demo.domain.source.datasource.wrapper.DataSourceId;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootTest
@Import(SeleniumTest.config.class)
class SeleniumTest {

    @Autowired
    private Selenium selenium;

    @BeforeEach
    public void init() {
        Source source = new Source();
        source.add("sourceId", new DataSourceId("1TiTPEwUKlO450tUz5pLiTehCDc320INlJzhVTZOTrLA"));
        selenium.startWebDriver(source);
    }

    @Test
    public void getSpreadSheetData() {
        selenium.getSpreadSheetData();
    }

    @Test
    public void setSpreadSheetData(){
        selenium.setSpreadSheetData();
    }

    @TestConfiguration
    static class config{
    }

}