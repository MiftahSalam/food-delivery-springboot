package com.example.fooddeliverysystem.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.example.fooddeliverysystem.entity.TypeEntity;
import com.example.fooddeliverysystem.model.dto.TypeDTO;
import com.example.fooddeliverysystem.model.dto.UpdateTypeDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.model.response.TypeResponse;
import com.example.fooddeliverysystem.repository.TypeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TypeControllerTest extends BaseIntegrationTest {
    @Autowired
    private TypeRepository typeRepository;

    private List<TypeEntity> typeEntities = new ArrayList<>();

    final double incrPrice = 12.43;

    @BeforeEach
    void setUp() {
        super.setUp();

        typeEntities.clear();
        for (int i = 0; i < 10; i++) {
            TypeEntity typeEntity = new TypeEntity();
            typeEntity.setName("type test");
            typeEntity.setPrice(10.32 + incrPrice * i);
            typeEntity.setReguler(i < 5);

            typeEntities.add(typeEntity);
        }

        typeEntities = typeRepository.saveAll(typeEntities);
    }

    @AfterEach
    void cleanUp() {
        super.cleanUp();
        typeRepository.deleteAll();
    }

    @Test
    void testGetTypes() throws JsonMappingException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + adminToken);
        HttpEntity<String> request = new HttpEntity<String>("", headers);

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/types/all",
                HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        BaseResponse<List<TypeResponse>> typeResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<BaseResponse<List<TypeResponse>>>() {

                });
        assertEquals(10, typeResponse.getData().size());
    }

    @Test
    void testGetRegularTypes() throws JsonMappingException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + adminToken);
        HttpEntity<String> request = new HttpEntity<String>("", headers);

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/types/regular",
                HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        BaseResponse<List<TypeResponse>> typeResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<BaseResponse<List<TypeResponse>>>() {

                });
        assertEquals(5, typeResponse.getData().size());
    }

    @Test
    void testGetType() throws JsonMappingException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + adminToken);
        HttpEntity<String> request = new HttpEntity<String>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/types/" + typeEntities.get(0).getId(),
                HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        BaseResponse<TypeResponse> typeResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<BaseResponse<TypeResponse>>() {

                });
        assertEquals(typeEntities.get(0).getId(), typeResponse.getData().getId());
        assertEquals(typeEntities.get(0).getName(), typeResponse.getData().getName());
        assertEquals(typeEntities.get(0).getPrice(), typeResponse.getData().getPrice(), 0.01);
    }

    @Test
    void testDeleteType() throws JsonMappingException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + adminToken);
        HttpEntity<String> request = new HttpEntity<String>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/types/" + typeEntities.get(0).getId(),
                HttpMethod.DELETE, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<TypeEntity> types = typeRepository.findAll();
        assertEquals(9, types.size());

        response = restTemplate.exchange(
                "http://localhost:" + port + "/types/0",
                HttpMethod.DELETE, request, String.class);

        assertEquals(HttpStatus.NOT_MODIFIED, response.getStatusCode());
        types = typeRepository.findAll();
        assertEquals(9, types.size());
    }

    @Test
    void testInsertType() throws JsonMappingException, JsonProcessingException {
        TypeDTO typeDTO = new TypeDTO("test new type", 1.0, true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        HttpEntity<TypeDTO> request = new HttpEntity<TypeDTO>(typeDTO, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/types", HttpMethod.POST, request,
                String.class);
        BaseResponse<TypeResponse> typeResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<BaseResponse<TypeResponse>>() {

                });

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertDoesNotThrow(() -> {
            assertNotNull(typeResponse.getData());
            assertEquals("test new type", typeResponse.getData().getName());

        }, "access null pointer");

        List<TypeEntity> types = typeRepository.findAll();
        assertEquals(11, types.size());
    }

    @Test
    void testUpdateType() throws JsonMappingException, JsonProcessingException {
        UpdateTypeDTO typeDTO = new UpdateTypeDTO(typeEntities.get(0).getId(), "test update type", 1.0, true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        HttpEntity<UpdateTypeDTO> request = new HttpEntity<UpdateTypeDTO>(typeDTO, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/types", HttpMethod.PUT, request,
                String.class);
        BaseResponse<TypeResponse> typeResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<BaseResponse<TypeResponse>>() {

                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertDoesNotThrow(() -> {
            assertNotNull(typeResponse.getData());
            assertEquals("test update type", typeResponse.getData().getName());

        }, "access null pointer");

        TypeEntity type = typeRepository.findById(typeEntities.get(0).getId()).orElse(null);
        assertEquals("test update type", type.getName());
    }
}
