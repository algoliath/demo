package com.example.demo.domain.datasource.source.wrapper;

public enum MimeTypes {

    DOCS("application/vnd.google-apps.document"),
    SHEETS("application/vnd.google-apps.spreadsheet");

    private final String description;

    private MimeTypes(String description){
         this.description = description;
    }

    @Override
    public String toString(){
        return description;
    }

}
