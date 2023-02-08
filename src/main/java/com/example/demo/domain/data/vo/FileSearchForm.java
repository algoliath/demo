package com.example.demo.domain.data.vo;

import com.example.demo.domain.source.datasource.wrapper.MimeTypes;
import lombok.Data;

@Data
public class FileSearchForm {

    private String fileName;

    private MimeTypes mimeTypes;

}
