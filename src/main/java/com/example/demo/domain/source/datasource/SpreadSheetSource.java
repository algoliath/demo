package com.example.demo.domain.source.datasource;

import com.example.demo.domain.columnTable.SpreadSheetTable;
import com.example.demo.util.Source;
import com.example.demo.domain.source.datasource.wrapper.MimeTypes;
import com.example.demo.domain.source.datasource.wrapper.DataSourceId;
import com.example.demo.util.datasource.CredentialUtils;
import com.example.demo.util.datasource.connection.SheetsConnection;
import com.google.api.services.drive.model.*;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Slf4j
@Data
public class SpreadSheetSource implements DataSource {

    public static String SPREAD_SHEET_VALUES = "values";
    private final String credentials;

    public SpreadSheetSource(String fileName) {
        this.credentials = CredentialUtils.getCredentialPath(fileName);
    }

    public void writeSpreadSheetTable(Source<String> paramSource, List<List<Object>> values){
        String range = paramSource.get(DataSource.FILE_RANGE).orElse(null);
        DataSourceId dataSourceId = getDataSourceId(paramSource);
        try {
            Sheets service = SheetsConnection.getConnection(credentials);
            ValueRange valueRange = new ValueRange();
            valueRange.setValues(values);
            service.spreadsheets().values().update(dataSourceId.getOriginalFileId(), range, valueRange);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // get single spreadsheet file
    public Source<SpreadSheetTable> getSpreadSheetTable(Source<String> paramSource) throws IOException {
        String range = paramSource.get(DataSource.FILE_RANGE).orElse(null);
        List<DataSourceId> dataSourceIds = getDataSourceIds(paramSource);
        Source<SpreadSheetTable> spreadSheetTableSource = new Source<>();
        try {
            Sheets service = SheetsConnection.getConnection(credentials);
            for(DataSourceId dataSourceId: dataSourceIds) {
                List<List<Object>> values = service.spreadsheets().values().get(dataSourceId.getOriginalFileId(), range).execute().getValues();
                spreadSheetTableSource.add(SPREAD_SHEET_VALUES, new SpreadSheetTable(range, values));
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        return spreadSheetTableSource;
    }

    public Source<RevisionList> getRevisionsSource(Source<String> paramSource){
        String title = paramSource.get(DataSource.FILE_NAME).orElse(null);
        String fileId = getDataSourceIds(paramSource).get(0).getOriginalFileId();
        String sign = paramSource.get(DataSource.PATTERN_MATCHER).orElse("=");
        Source<RevisionList> revisionListSource = new DriveSource.Builder(MimeTypes.SHEETS)
                .setTitle(title)
                .setPattern(sign)
                .setFields("id", "publishedLink")
                .setCredentials(credentials)
                .build().getRevisionSource(fileId);
        return revisionListSource;
    }

    public Source<PermissionList> getPermissionsSource(Source<String> paramSource){
        String title = paramSource.get(DataSource.FILE_NAME).orElse(null);
        String fileId = getDataSourceIds(paramSource).get(0).getOriginalFileId();
        String sign = paramSource.get(DataSource.PATTERN_MATCHER).orElse("=");
        Source<PermissionList> permissionListSource = new DriveSource.Builder(MimeTypes.SHEETS)
                .setTitle(title)
                .setPattern(sign)
                .setFields("id")
                .setCredentials(credentials)
                .build().getPermissionSource(fileId);
        return permissionListSource;
    }

    public DataSourceId getDataSourceId(Source<String> paramSource){
        List<DataSourceId> results = getDataSourceIds(paramSource);
        if(results.size() != 1){
            throw new IllegalStateException("결과 값이 하나 이상 존재합니다");
        }
        return results.get(0);
    }

    public List<DataSourceId> getDataSourceIds(Source<String> paramSource){
        String title = paramSource.get(DataSource.FILE_NAME).orElse(null);
        String pattern = paramSource.get(DataSource.PATTERN_MATCHER).orElse("=");
        return new DriveSource.Builder(MimeTypes.SHEETS)
                .setTitle(title)
                .setPattern(pattern)
                .setFields("id", "name")
                .setCredentials(credentials)
                .build().getSourceIds();
    }

    public List<File> getFiles(Source<String> paramSource){
        String title = paramSource.get(DataSource.FILE_NAME).orElse(null);
        String pattern = paramSource.get(DataSource.PATTERN_MATCHER).orElse("=");
        return new DriveSource.Builder(MimeTypes.SHEETS)
                .setTitle(title)
                .setFields("id", "name")
                .setPattern(pattern)
                .setCredentials(credentials)
                .build().getSource();
    }

    public void updatePermission(Source<String> paramSource, Permission permission) {
        List<DataSourceId> dataSourceIds = getDataSourceIds(paramSource);
        String title = paramSource.get(DataSource.FILE_NAME).orElse(null);
        String pattern = paramSource.get(DataSource.PATTERN_MATCHER).orElse("=");
        new DriveSource.Builder(MimeTypes.SHEETS)
                .setTitle(title)
                .setFields("id", "name")
                .setPattern(pattern)
                .setCredentials(credentials)
                .build().updatePermission(dataSourceIds.get(0).getOriginalFileId(), permission);

    }

    public void updateRevision(Source<String> paramSource, Revision revision) {
        List<DataSourceId> dataSourceIds = getDataSourceIds(paramSource);
        String title = paramSource.get(DataSource.FILE_NAME).orElse(null);
        String pattern = paramSource.get(DataSource.PATTERN_MATCHER).orElse("=");
        new DriveSource.Builder(MimeTypes.SHEETS)
                .setTitle(title)
                .setFields("id", "name")
                .setPattern(pattern)
                .setCredentials(credentials)
                .build().updateRevision(dataSourceIds.get(0).getOriginalFileId(), revision);
    }

    public DataSourceId publish(Source<String> paramSource, BindingResult bindingResult) {
        List<DataSourceId> dataSourceIds = getDataSourceIds(paramSource); // 파일 id 소스
        if(dataSourceIds.isEmpty()){
            throw new IllegalArgumentException("파일을 찾을 수 없습니다");
        }

        List<Revision> revisions = getRevisionsSource(paramSource).get("revisions").get().getRevisions();
        Revision lastRevision = revisions.get(revisions.size()-1);
        log.info("lastRevision={}", lastRevision);

        Revision revision = new Revision();
        revision.setId(revisions.get(revisions.size()-1).getId());
        revision.setPublishedOutsideDomain(true);
        revision.setPublished(true);
        revision.setPublishAuto(true);
        updateRevision(paramSource, revision);

        Permission permission = new Permission();
        permission.setType("anyone");
        permission.setRole("writer");
        updatePermission(paramSource, permission);
        return dataSourceIds.get(0);
    }

    public Sheet getSpreadSheet(File file){
        Sheet sheet;
        try{
            Sheets sheets = SheetsConnection.getConnection(credentials);
            Spreadsheet target = sheets.spreadsheets().get(file.getId()).execute();
            List<Sheet> sheetList = target.getSheets();
            sheet = sheetList.get(0);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sheet;
    }

    public File createFile(Source<String> paramSource){
        String title = paramSource.get(DataSource.FILE_NAME).orElse(null);
        String pattern = paramSource.get(DataSource.PATTERN_MATCHER).orElse("=");
        File fileMetadata = new File();
        fileMetadata.setName(title);
        fileMetadata.setMimeType(MimeTypes.SHEETS.toString());
        File file = new DriveSource.Builder(MimeTypes.SHEETS)
                .setTitle(title)
                .setFields("id", "name")
                .setPattern(pattern)
                .setCredentials(credentials)
                .build().createFile(fileMetadata);
        return file;
    }


}
