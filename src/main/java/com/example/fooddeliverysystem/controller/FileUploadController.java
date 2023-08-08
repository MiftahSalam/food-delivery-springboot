package com.example.fooddeliverysystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.service.FileUpload;

@RestController
@RequestMapping("/files")
public class FileUploadController {
    @Autowired
    private FileUpload fileUpload;

    @PostMapping("/{fileType}")
    public ResponseEntity<BaseResponse<String>> uploadImage(@PathVariable("fileType") String fileType,
            @RequestBody MultipartFile file) {
        String uploadFile = fileUpload.uploadFile(file, fileType);
        if (uploadFile == null) {
            return new ResponseEntity<>(BaseResponse.<String>builder()
                    .data(null)
                    .status("failed")
                    .message("failed to upload file")
                    .build(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(BaseResponse.<String>builder()
                .data(uploadFile)
                .status("ok")
                .message("success to upload file")
                .build(), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Resource> getImage(@RequestParam String imagePath) {
        Resource file = fileUpload.getFile(imagePath);
        if (file == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(file);
    }
}
