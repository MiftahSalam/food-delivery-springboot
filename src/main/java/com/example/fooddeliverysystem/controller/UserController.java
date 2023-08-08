package com.example.fooddeliverysystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fooddeliverysystem.model.dto.UserDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.service.user.UserService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<UserDTO>> getUser(@PathVariable("userId") Integer userId) {
        UserDTO user = userService.getUser(userId);
        if (user != null) {
            return new ResponseEntity<>(BaseResponse.<UserDTO>builder()
                    .data(user)
                    .status("success to get user")
                    .message("ok")
                    .build(), HttpStatus.OK);

        }

        return new ResponseEntity<>(BaseResponse.<UserDTO>builder()
                .data(null)
                .status("failed to get user")
                .message("failed")
                .build(), HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        if (users != null) {
            return new ResponseEntity<>(BaseResponse.<List<UserDTO>>builder()
                    .data(users)
                    .status("success to get all user")
                    .message("ok")
                    .build(), HttpStatus.OK);

        }

        return new ResponseEntity<>(BaseResponse.<List<UserDTO>>builder()
                .data(null)
                .status("failed to get all user")
                .message("failed")
                .build(), HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/email/{email}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<UserDTO>> getUserByEmail(@PathVariable("email") String email) {
        UserDTO user = userService.getUserByEmail(email);
        if (user != null) {
            return new ResponseEntity<>(BaseResponse.<UserDTO>builder()
                    .data(user)
                    .status("success to get user")
                    .message("ok")
                    .build(), HttpStatus.OK);

        }

        return new ResponseEntity<>(BaseResponse.<UserDTO>builder()
                .data(null)
                .status("failed to get user")
                .message("failed")
                .build(), HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin
    @PutMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateUserEmail(@PathVariable("userId") Long userId, @RequestBody String email) {
        return userService.updateUserEmail(userId.intValue(), email);
    }

    @CrossOrigin
    @PutMapping("/{userId}/status/{command}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<String>> updateUserStatus(@PathVariable("userId") Long userId,
            @PathVariable("command") String command) {
        if (userService.updateUser(userId.intValue(), command)) {
            return new ResponseEntity<>(BaseResponse.<String>builder()
                    .data("user updated")
                    .status("success to update user")
                    .message("ok")
                    .build(), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(BaseResponse.<String>builder()
                .data(null)
                .status("failed to update user")
                .message("failed")
                .build(), HttpStatus.NOT_MODIFIED);
    }

    @CrossOrigin
    @PutMapping("/{userId}/image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateUserImage(@PathVariable("userId") Long userId, @RequestBody String image) {
        return userService.updateUserImage(userId.intValue(), image);
    }

    @CrossOrigin
    @PutMapping("/updatePassword/{token}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateUserPassword(@PathVariable("token") String token,
            @RequestBody String password) {
        return userService.updateUserPassword(token, password);
    }
}
