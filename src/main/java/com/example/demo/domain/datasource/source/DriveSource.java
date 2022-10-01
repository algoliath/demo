package com.example.demo.domain.datasource.source;
import com.example.demo.domain.datasource.Source;
import com.example.demo.domain.datasource.source.wrapper.MimeTypes;
import com.example.demo.domain.datasource.source.wrapper.SourceId;
import com.example.demo.util.connection.DriveConnection;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DriveSource implements ParameterSource {

    private String title;
    private String parameters;
    private MimeTypes mimeTypes;
    private String lastModified;

    private DriveSource(Builder builder){
        title = builder.title;
        parameters = builder.parameters;
        mimeTypes = builder.mimeTypes;
        lastModified = builder.lastModifiedTime;
    }

    @Override
    public Source getSource(Source dataSource) {

        List<SourceId> sourceIds = new ArrayList<>();
        List<File> files;

        String pageToken = null;
        try {
            do{
                Drive service = DriveConnection.getConnection(dataSource);
                FileList result = service.files().list()
                        .setQ("mimeType = '" + mimeTypes + "'")
                        .setQ("name = '" + title + "'")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(" + parameters + ")")
                        .setPageToken(pageToken)
                        .execute();
                files = result.getFiles();

                if(files!=null){
                    for(File file: files){
                        sourceIds.add(new SourceId(file.getId()));
                    }
                }
                pageToken = result.getNextPageToken();

            } while(pageToken != null);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Source source = new Source();
        source.setData("sourceId", sourceIds);
        return source;
    }

    public static class Builder{

        private String title;
        private String parameters;
        private MimeTypes mimeTypes;
        private String lastModifiedTime;

        public Builder(MimeTypes mimeTypes){
            this.mimeTypes = mimeTypes;
        }

        public Builder setLastModified(String lastModifiedTime){
            this.lastModifiedTime = lastModifiedTime;
            return this;
        }

        public Builder setTitle(String title){
            this.title = title;
            return this;
        }

        public Builder setParameters(String... varargs){
            String parameters = "";
            for(String var: varargs){
                parameters += var;
                parameters += ",";
            }
            parameters = parameters.substring(0, parameters.length()-1);
            this.parameters = parameters;
            log.info("parameters={}", parameters);
            return this;
        }

        public DriveSource build(){
            return new DriveSource(this);
        }

    }

}

