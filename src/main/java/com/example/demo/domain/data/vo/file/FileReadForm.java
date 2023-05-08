package com.example.demo.domain.data.vo.file;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileReadForm {

    private String fileId;
    private String fileName;
    private String fileUrl;

    public FileReadForm(String fileId, String fileName, String fileUrl) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
}
