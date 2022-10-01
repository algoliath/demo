package com.example.demo.domain.datasource.source;

import com.example.demo.domain.datasource.Source;
import com.example.demo.domain.datasource.parser.DocsParser;
import com.example.demo.domain.datasource.source.wrapper.MimeTypes;
import com.example.demo.domain.datasource.source.wrapper.SourceId;
import com.example.demo.util.connection.DocsConnection;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DocsSource implements ParameterSource {

    private final DocsParser docsParser;

    @Override
    public Source getSource(Source dataSource) {

        Source driveSource = new DriveSource.Builder(MimeTypes.DOCS).
                            setTitle("DocsSource").
                            setParameters("id", "name").build().getSource(dataSource);

        List<SourceId> sourceIds = (List<SourceId>) driveSource.getData("sourceId").orElse(new ArrayList<>());
        List<Source> sources = new ArrayList<>();

        Docs service;
        Source docsSource;

        try {
            service = DocsConnection.getConnection(dataSource);
            for(SourceId sourceId: sourceIds){
                Document doc = service.documents().get(sourceId.getFileId()).execute();
                docsSource = new Source();
                docsSource.setData("tableElement", doc.getBody().getContent().get(2));
                docsSource.setData("sourceId", sourceId);
                log.info("tableElement={}", docsSource.getData("tableElement"));
                sources.add(docsSource);
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        docsSource = new Source();
        docsSource.setData("tableElements", docsParser.map(sources));
        return docsSource;
    }
}

