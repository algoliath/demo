package com.example.demo.domain.source.datasource;

import com.example.demo.domain.member.Member;
import com.example.demo.util.Source;
import com.example.demo.domain.source.datasource.wrapper.MimeTypes;
import com.example.demo.domain.source.datasource.wrapper.DataSourceId;
import com.example.demo.domain.source.datasource.connection.DocsConnection;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DocsSource implements DataSource {

    private final String credentials;

    public DocsSource(Member member){
        this.credentials = "/auth/"+ member.getFileId();
    }

    public Source<DataSourceId> getDataSource(Source<String> paramSource) {
        List<DataSourceId> dataSourceIds = new DriveSource.Builder(MimeTypes.DOCS)
                .setTitle("DocsSource")
                .setFields("id", "name")
                .setCredentials(credentials)
                .build().getSourceIds();

        Source docsSource = null;
        try {
            for(DataSourceId dataSourceId: dataSourceIds){
                Docs service = DocsConnection.getConnection(credentials);
                Document doc = service.documents().get(dataSourceId.getOriginalFileId()).execute();
                docsSource = new Source();
                docsSource.add("tableElement", doc.getBody().getContent().get(2));
                docsSource.add("sourceId", dataSourceId);
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return docsSource;
    }
}

