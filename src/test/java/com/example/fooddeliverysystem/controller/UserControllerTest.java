package com.example.fooddeliverysystem.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.model.dto.UserDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.repository.ConfirmationTokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public class UserControllerTest extends BaseIntegrationTest {
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
        void testGetUser() throws JsonMappingException, JsonProcessingException {
                UserEntity user = userRepository.findFirstByEmail("user@example.com").orElse(null);
                UserEntity admin = userRepository.findFirstByEmail("admin@example.com").orElse(null);

                assertNotNull(user);
                assertNotNull(admin);

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + adminToken);

                HttpEntity<String> request = new HttpEntity<String>("", headers);
                ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + port + "/users/" + user.getId(), HttpMethod.GET, request,
                                String.class);
                BaseResponse<UserDTO> userResponse = objectMapper.readValue(response.getBody(),
                                new TypeReference<BaseResponse<UserDTO>>() {

                                });

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(userResponse.getData());
                assertEquals(user.getName(), userResponse.getData().getName());

                response = restTemplate.exchange(
                                "http://localhost:" + port + "/users/email/" + user.getEmail(), HttpMethod.GET, request,
                                String.class);
                userResponse = objectMapper.readValue(response.getBody(),
                                new TypeReference<BaseResponse<UserDTO>>() {

                                });

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(userResponse.getData());
                assertEquals(user.getName(), userResponse.getData().getName());
        }

        @Test
        void testGetAllUser() throws JsonMappingException, JsonProcessingException {
                UserEntity user = userRepository.findFirstByEmail("user@example.com").orElse(null);
                UserEntity admin = userRepository.findFirstByEmail("admin@example.com").orElse(null);

                assertNotNull(user);
                assertNotNull(admin);

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + adminToken);

                HttpEntity<String> request = new HttpEntity<String>("", headers);
                ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + port + "/users/all", HttpMethod.GET, request,
                                String.class);
                BaseResponse<List<UserDTO>> userResponse = objectMapper.readValue(response.getBody(),
                                new TypeReference<BaseResponse<List<UserDTO>>>() {

                                });

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(userResponse.getData());
                assertEquals(2, userResponse.getData().size());
        }

        @Test
        void testUpdateUserEmail() throws JsonMappingException, JsonProcessingException {
                UserEntity user = userRepository.findFirstByEmail("user@example.com").orElse(null);

                assertNotNull(user);

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + adminToken);

                HttpEntity<String> request = new HttpEntity<String>("user_update@example.com", headers);
                ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + port + "/users/" + user.getId(), HttpMethod.PUT, request,
                                String.class);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("Success change email. You need to login again", response.getBody());
        }

        @Test
        void testUpdateUserStatus() throws JsonMappingException, JsonProcessingException {
                UserEntity user = userRepository.findFirstByEmail("user@example.com").orElse(null);

                assertNotNull(user);

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + adminToken);

                HttpEntity<String> request = new HttpEntity<String>("", headers);
                ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + port + "/users/" + user.getId() + "/status/BAN", HttpMethod.PUT,
                                request,
                                String.class);

                assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

                user = userRepository.findFirstByEmail("user@example.com").orElse(null);

                assertNotNull(user);
                assertEquals("BANNED", user.getStatus().name());
        }

        @Test
        void testUpdateUserImage() throws JsonMappingException, JsonProcessingException {
                UserEntity user = userRepository.findFirstByEmail("user@example.com").orElse(null);

                assertNotNull(user);

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + adminToken);

                HttpEntity<String> request = new HttpEntity<String>("http://image_update1.png", headers);
                ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + port + "/users/" + user.getId() + "/image", HttpMethod.PUT,
                                request,
                                String.class);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("Image updated successfully", response.getBody());

                user = userRepository.findFirstByEmail("user@example.com").orElse(null);

                assertNotNull(user);
                assertEquals("http://image_update1.png", user.getImagePath());

        }
}
