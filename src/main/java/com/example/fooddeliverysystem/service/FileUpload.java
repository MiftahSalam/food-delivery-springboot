package com.example.fooddeliverysystem.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileUpload {
    String uploadFile(MultipartFile uploadFile, String fileType);

    Resource getFile(String imageName);
}
