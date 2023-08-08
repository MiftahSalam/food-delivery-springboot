package com.example.fooddeliverysystem.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.service.NotificationService;

@RestController
@RequestMapping("notif")
public class NotifController {
    @Autowired
    private NotificationService notificationService;

    @CrossOrigin
    @PostMapping("/token/{token}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<String>> saveTokenForNotif(@PathVariable("token") String token,
            @RequestHeader("Authorization") String authorization) throws IOException {
        UserEntity user = notificationService.getUser(token);
        if (user == null) {
            return new ResponseEntity<>(BaseResponse.<String>builder()
                    .data("You must sign in to be able to order meals.")
                    .status("failed")
                    .message("invalid token")
                    .build(), HttpStatus.UNAUTHORIZED);
        }
        if (notificationService.saveToken(token, user)) {
            return new ResponseEntity<>(BaseResponse.<String>builder()
                    .data("Token saved successfully")
                    .status("ok")
                    .message("Token saved")
                    .build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(BaseResponse.<String>builder()
                    .data(null)
                    .status("failed")
                    .message("failed to save token")
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }
}
