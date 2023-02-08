package com.example.demo.domain.source.datasource;
import com.example.demo.util.Source;
import com.example.demo.domain.source.datasource.wrapper.MimeTypes;
import com.example.demo.domain.source.datasource.wrapper.DataSourceId;
import com.example.demo.util.datasource.connection.DriveConnection;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class DriveSource {

    private String title;
    private String pattern;
    private String fields;
    private MimeTypes mimeTypes;
    private String lastModified;
    private String credentials;
    private Drive service;

    private DriveSource(Builder builder){
        title = builder.title;
        pattern = builder.pattern;
        fields = builder.fields;
        mimeTypes = builder.mimeTypes;
        lastModified = builder.lastModifiedTime;
        credentials = builder.credentials;
        service = DriveConnection.getConnection(credentials);
    }

    public List<File> getSource(){
        List<File> files;
        List<File> fileList = new ArrayList<>();
        String pageToken = null;
        try {
            do{
                Drive.Files.List list = service.files().list();
                if(StringUtils.hasText(pattern) && StringUtils.hasText(title)){
                    list = list.setQ("mimeType = '" + mimeTypes + "' and name " + pattern + " '" + title + "'");
                }
                FileList result = list.setSpaces("drive")
                        .setFields("nextPageToken, files(" + fields + ")")
                        .setPageToken(pageToken)
                        .execute();
                files = result.getFiles();
                for(File file: files){
                    fileList.add(file);
                }
                pageToken = result.getNextPageToken();
            } while(pageToken != null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally{
            return fileList;
        }
    }


    public List<DataSourceId> getSourceIds() {
        List<File> files;
        List<DataSourceId> sourceIdList = new ArrayList<>();
        String pageToken = null;
        try {
            do{
                FileList result = service.files().list()
                        .setQ("mimeType = '" + mimeTypes + "'")
                        .setQ("name = '" + title + "'")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id)")
                        .setPageToken(pageToken)
                        .execute();
                files = result.getFiles();
                if(files!=null){
                    for(File file: files){
                         sourceIdList.add(new DataSourceId(file.getId()));
                    }
                }
                pageToken = result.getNextPageToken();
            } while(pageToken != null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally{
            return sourceIdList;
        }
    }

    public Source<RevisionList> getRevisionSource(String fileId){
        try {;
            RevisionList revisionList = service.revisions().list(fileId).execute();
            Source<RevisionList> source = new Source<>(Arrays.asList("revisions"), Arrays.asList(revisionList));
            return source;
        } catch (IOException e) {
            System.out.println("An error occurred during revision retrieval: " + e);
        }
        return new Source<>();
    }

    public Source<PermissionList> getPermissionSource(String fileId) {
        PermissionList permissionList = null;
        try {
            permissionList = service.permissions().list(fileId).execute();
        } catch (IOException e) {
            System.out.println("An error occurred during permission retrieval: " + e);
        }
        Source<PermissionList> source = new Source<>(Arrays.asList("permissions"), Arrays.asList(permissionList));
        return source;
    }

    public void updatePermission(String fileId, Permission permission) {
        try {
            service.permissions().create(fileId, permission).execute();
        } catch (IOException e) {
            System.out.println("An error occurred during revision update: " + e);
        }
    }

    public void updateRevision(String fileId, Revision revision) {
        try {
            service.revisions().update(fileId, revision.getId(), revision).execute();
        } catch (IOException e) {
            System.out.println("An error occurred during revision update: " + e);
        }
    }

    public File createFile(File fileMetadata){
        File file;
        try {
            file = service.files().create(fileMetadata)
                   .setFields("id")
                   .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    public static class Builder{

        private String title;
        private String pattern;
        private String fields;
        private MimeTypes mimeTypes;
        private String lastModifiedTime;
        private String credentials;

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

        public Builder setPattern(String pattern){
            this.pattern = pattern;
            return this;
        }

        public Builder setFields(String... varargs){
            String parameters = "";
            for(String var: varargs){
                parameters += var;
                parameters += ",";
            }
            parameters = parameters.substring(0, parameters.length()-1);
            this.fields = parameters;
            log.info("parameters={}", parameters);
            return this;
        }

        public Builder setCredentials(String credentials){
            this.credentials = credentials;
            return this;
        }

        public DriveSource build(){
            return new DriveSource(this);
        }

    }

}

