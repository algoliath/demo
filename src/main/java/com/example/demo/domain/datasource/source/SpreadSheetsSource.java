package com.example.demo.domain.datasource.source;

import com.example.demo.domain.datasource.Source;
import com.example.demo.domain.datasource.parser.SpreadSheetsParser;
import com.example.demo.domain.datasource.source.wrapper.MimeTypes;
import com.example.demo.domain.datasource.source.wrapper.SourceId;
import com.example.demo.util.connection.SheetsConnection;
import com.google.api.services.sheets.v4.Sheets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpreadSheetsSource implements ParameterSource {

    private final SpreadSheetsParser spreadSheetsParser;

    // get single spreadsheet file
    @Override
    public Source<List<List<Object>>> getSource(Source dataSource) {

        String title = (String) dataSource.getData("spreadSheetTitle").orElse("title");
        String range = (String) dataSource.getData("spreadSheetRange").orElse("range");

        Source driveSource = new DriveSource.Builder(MimeTypes.SHEETS)
                .setTitle(title)
                .setParameters("id", "name").build().getSource(dataSource);

        List<SourceId> sourceIds = (List<SourceId>) driveSource.getData("sourceId").orElse(new ArrayList<>());
        List<Source> sources = new ArrayList<>();

        Sheets service;
        Source sheetsSource;

        try {
            service = SheetsConnection.getConnection(dataSource);
            for(SourceId sourceId: sourceIds){
                sheetsSource = new Source();
                sheetsSource.setData("spreadTableElement",
                                          service.spreadsheets().values()
                                                  .get(sourceId.getFileId(), range)
                                                  .execute().getValues());
                sheetsSource.setData("sourceId", sourceId);
                sources.add(sheetsSource);
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sheetsSource = new Source();
        sheetsSource.setData("spreadTableElements", spreadSheetsParser.parse(sources));
        return sheetsSource;
    }

}
