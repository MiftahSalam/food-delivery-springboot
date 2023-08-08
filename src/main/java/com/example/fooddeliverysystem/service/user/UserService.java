package com.example.fooddeliverysystem.service.user;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.model.dto.UserDTO;
import com.example.fooddeliverysystem.model.dto.UserRegisterDTO;

public interface UserService {
    UserEntity registerUser(UserRegisterDTO user);

    UserEntity confirmUserAccount(String token);

    UserDTO getUser(int id);

    List<UserDTO> getAllUsers();

    boolean updateUser(int id, String command);

    ResponseEntity<String> updateUserEmail(int id, String email);

    ResponseEntity<String> updateUserImage(int id, String imagePath);

    ResponseEntity<String> updateUserPassword(String token, String password);

    UserDTO getUserByEmail(String email);

    UserEntity resetPassoword(String email);
}
