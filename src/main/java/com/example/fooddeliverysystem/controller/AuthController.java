package com.example.fooddeliverysystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.model.dto.LoginDTO;
import com.example.fooddeliverysystem.model.dto.UserDTO;
import com.example.fooddeliverysystem.model.dto.UserRegisterDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.model.response.LoginResponse;
import com.example.fooddeliverysystem.security.TokenUtils;
import com.example.fooddeliverysystem.service.user.UserService;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserService userService;

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody @Valid LoginDTO loginDTO) {
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDTO.email(),
                    loginDTO.password());
            Authentication authenticate = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            UserDetails loadUserByUsername = userDetailsService.loadUserByUsername(loginDTO.email());
            UserDTO userDTO = userService.getUserByEmail(loginDTO.email());

            return new ResponseEntity<>(BaseResponse.<LoginResponse>builder()
                    .data(LoginResponse.builder()
                            .userid(userDTO.getId())
                            .email(loadUserByUsername.getUsername())
                            .imagePath(userDTO.getImagePath())
                            .lastName(userDTO.getLastName())
                            .name(userDTO.getName())
                            .role(userDTO.getRole())
                            .token(tokenUtils.generateToken(loadUserByUsername)).build())
                    .status("ok")
                    .message("login success")
                    .build(),
                    HttpStatus.OK);

        } catch (ValidationException e) {
            e.printStackTrace();

            return new ResponseEntity<>(BaseResponse.<LoginResponse>builder()
                    .data(null)
                    .status("failed")
                    .message("invalid username or password")
                    .build(),
                    HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>(BaseResponse.<LoginResponse>builder()
                    .data(null)
                    .status("failed")
                    .message("login failed")
                    .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<String>> register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        UserEntity registerUser = userService.registerUser(userRegisterDTO);
        if (registerUser == null) {
            return new ResponseEntity<>(BaseResponse.<String>builder()
                    .data(null)
                    .status("failed")
                    .message("this email already exist")
                    .build(),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(BaseResponse.<String>builder()
                .data("You need to verify your account. Please check your email.")
                .status("ok")
                .message("user alredy created")
                .build(),
                HttpStatus.CREATED);
    }

    @CrossOrigin
    @PostMapping("/resetPassword")
    public ResponseEntity<BaseResponse<String>> resetPassword(@RequestBody String email) {
        UserEntity registerUser = userService.resetPassoword(email);
        if (registerUser == null) {
            return new ResponseEntity<>(BaseResponse.<String>builder()
                    .data(null)
                    .status("failed")
                    .message("this email doesn't exist")
                    .build(),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(BaseResponse.<String>builder()
                .data("You need to verify your account. Please check your email.")
                .status("ok")
                .message("verify account")
                .build(),
                HttpStatus.CREATED);
    }

    @CrossOrigin
    @PostMapping("/confirmAccount")
    public ResponseEntity<BaseResponse<String>> confirmAccount(@RequestParam("token") String token) {
        UserEntity registerUser = userService.confirmUserAccount(token);
        if (registerUser == null) {
            return new ResponseEntity<>(BaseResponse.<String>builder()
                    .data(null)
                    .status("failed")
                    .message("invalid token")
                    .build(),
                    HttpStatus.NOT_MODIFIED);
        }

        return new ResponseEntity<>(BaseResponse.<String>builder()
                .data("Account verified ok")
                .status("ok")
                .message("success confirm account")
                .build(),
                HttpStatus.OK);

    }
}
