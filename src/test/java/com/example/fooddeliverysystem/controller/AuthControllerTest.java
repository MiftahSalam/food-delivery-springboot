package com.example.fooddeliverysystem.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.example.fooddeliverysystem.model.dto.LoginDTO;
import com.example.fooddeliverysystem.model.dto.UserRegisterDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.model.response.LoginResponse;
import com.example.fooddeliverysystem.repository.ConfirmationTokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

public class AuthControllerTest extends BaseControllerMockMVCTest {
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @AfterEach
    void cleanUp() {
        confirmationTokenRepository.deleteAll();
        super.cleanUp();
    }

    @Test
    void testLogin() throws JsonProcessingException, Exception {
        LoginDTO loginDTO = new LoginDTO("user@example.com", "123456");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO))
                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk()).andDo(result -> {
                    BaseResponse<LoginResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<BaseResponse<LoginResponse>>() {

                            });

                    assertNotNull(response.getData());
                    assertNotNull(response.getData().getToken());
                    assertEquals("user@example.com", response.getData().getEmail());
                    assertEquals("USER", response.getData().getRole());
                });

        loginDTO = new LoginDTO("admin@example.com", "rahasia");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO))
                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk()).andDo(result -> {
                    BaseResponse<LoginResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<BaseResponse<LoginResponse>>() {

                            });

                    assertNotNull(response.getData());
                    assertNotNull(response.getData().getToken());
                    assertEquals("admin@example.com", response.getData().getEmail());
                    assertEquals("ADMIN", response.getData().getRole());
                });
    }

    @Test
    void testRegister() throws JsonProcessingException, Exception {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("salam.miftah@gmail.com", "secret", "newUser", "baru",
                "https://iamge.default.png");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDTO)))
                .andExpectAll(status().isCreated()).andDo(result -> {
                    BaseResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<BaseResponse<String>>() {

                            });

                    assertNotNull(response.getData());
                    assertEquals("You need to verify your account. Please check your email.", response.getData());
                    assertNotNull(userRepository.findFirstByEmail("salam.miftah@gmail.com").orElse(null));
                });
    }
}
