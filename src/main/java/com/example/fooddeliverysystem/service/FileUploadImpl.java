package com.example.fooddeliverysystem.service;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadImpl implements FileUpload {
    private static final String WEEKLY_MENU_PATH = "./src/main/resources/webapp/upload/weeklymenuimages/";
    private static final String AVATAR_PATH = "./src/main/resources/webapp/upload/userimages/";

    @Override
    public String uploadFile(MultipartFile uploadFile, String fileType) {
        String url;
        switch (fileType.toUpperCase()) {
            case "WEEKLY-MENU":
                url = WEEKLY_MENU_PATH;
                break;
            case "AVATAR":
                url = AVATAR_PATH;
                break;

            default:
                return null;
        }

        Path filePath = Path.of(url, uploadFile.getOriginalFilename());
        try (OutputStream newOutputStream = Files.newOutputStream(filePath);) {
            newOutputStream.write(uploadFile.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return url + uploadFile.getOriginalFilename();

    }

    @Override
    public Resource getFile(String imageName) {
        try {
            return new InputStreamResource(new FileInputStream(imageName));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
