package com.example.demo.domain.source.datasource.wrapper;

import lombok.Data;

@Data
public class DataSourceId {

    private String originalFileId;

    public DataSourceId(String fileId) {
        this.originalFileId = fileId;
    }

}
