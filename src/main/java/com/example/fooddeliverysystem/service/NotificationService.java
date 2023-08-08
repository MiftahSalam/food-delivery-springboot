package com.example.fooddeliverysystem.service;

import java.io.IOException;

import com.example.fooddeliverysystem.entity.UserEntity;

public interface NotificationService {
    boolean saveToken(String token, UserEntity userEntity) throws IOException;

    UserEntity getUser(String token);
}
