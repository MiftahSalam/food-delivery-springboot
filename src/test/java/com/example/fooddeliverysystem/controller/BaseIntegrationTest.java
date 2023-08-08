package com.example.fooddeliverysystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BaseIntegrationTest extends BaseControllerTest {
    @Autowired
    protected TestRestTemplate restTemplate;

    @LocalServerPort
    protected Integer port;

    @Override
    void setUp() {
        super.setUp();
    }

    @Override
    void cleanUp() {
        super.cleanUp();
    }
}
