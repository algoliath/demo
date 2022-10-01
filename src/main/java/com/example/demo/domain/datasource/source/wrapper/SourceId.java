package com.example.demo.domain.datasource.source.wrapper;

import lombok.Data;

@Data
public class SourceId {

    String fileId;

    public SourceId(String fileId) {
        this.fileId = fileId;
    }

}
