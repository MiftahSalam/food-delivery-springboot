package com.example.fooddeliverysystem.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.fooddeliverysystem.repository.RoleRepository;
import com.example.fooddeliverysystem.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class BaseControllerMockMVCTest extends BaseControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @AfterEach
    void cleanUp() {
        super.cleanUp();
    }
}
