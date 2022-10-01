package com.example.demo.domain.repository.uploadFile;

import com.example.demo.domain.datasource.uploadFile.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@PropertySource("classpath:settings.properties")
public class UploadFileRepository {

    @Value("${file.dir}")
    private String fileDir;

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();
        for(MultipartFile multipartFile: multipartFiles){
            if(multipartFile!=null){
                UploadFile uploadFile = storeFile(multipartFile);
                storeFileResult.add(uploadFile);
            }
        }
        return storeFileResult;
    }
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {

        if(multipartFile.isEmpty()){
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        // image.png

        String uuid = UUID.randomUUID().toString();
        // #####.png

        String ext = extractExt(originalFilename);
        String storeFilename = uuid + "." + ext;

        multipartFile.transferTo(new File(getFullPath(storeFilename)));
        return new UploadFile(originalFilename, storeFilename);
    }

    private String getFullPath(String filename) {
        return fileDir + filename;
    }

    private String extractExt(String originalFileName) {
        int pos = originalFileName.lastIndexOf(".");
        String ext = originalFileName.substring(pos + 1);
        return ext;
    }


}