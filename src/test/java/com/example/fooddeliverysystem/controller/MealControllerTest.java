package com.example.fooddeliverysystem.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.entity.TypeEntity;
import com.example.fooddeliverysystem.model.dto.AddMealDTO;
import com.example.fooddeliverysystem.model.dto.EditMealDTO;
import com.example.fooddeliverysystem.model.dto.TypeDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.model.response.MealResponse;
import com.example.fooddeliverysystem.repository.MealRepository;
import com.example.fooddeliverysystem.repository.MealTypeRepository;
import com.example.fooddeliverysystem.repository.TypeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public class MealControllerTest extends BaseIntegrationTest {
    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MealTypeRepository mealTypeRepository;

    private List<TypeEntity> typeEntities = new ArrayList<>();

    private List<MealEntity> mealEntities = new ArrayList<>();

    final double incrPrice = 12.43;

    @BeforeEach
    void setUp() {
        super.setUp();

        typeEntities.clear();
        for (int i = 0; i < 10; i++) {
            TypeEntity typeEntity = new TypeEntity();
            typeEntity.setName("type test " + i);
            typeEntity.setPrice(10.32 + incrPrice * i);
            typeEntity.setReguler(i < 5);

            typeEntities.add(typeEntity);
        }

        typeEntities = typeRepository.saveAll(typeEntities);

        mealEntities.clear();
        mealEntities = List.of("menu 1", "menu 2", "menu 3").stream()
                .map(name -> {
                    MealEntity meal = new MealEntity();
                    meal.setName(name);
                    return meal;
                }).collect(Collectors.toList());

        mealRepository.saveAll(mealEntities);
    }

    @AfterEach
    void cleanUp() {
        super.cleanUp();
        mealTypeRepository.deleteAll();
        mealRepository.deleteAll();
        typeRepository.deleteAll();
    }

    @Test
    void testAddMeal() throws JsonMappingException, JsonProcessingException {
        List<TypeDTO> typeDTOs = new ArrayList<>();
        typeEntities.subList(3, 6).forEach(type -> {
            TypeDTO typeDTO = new TypeDTO(type.getName(), type.getPrice(), type.isReguler());

            typeDTOs.add(typeDTO);
        });
        AddMealDTO addMealDTO = new AddMealDTO("meal 1", "this is fried chicken", true, false, typeDTOs);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AddMealDTO> request = new HttpEntity<AddMealDTO>(addMealDTO, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/meals", request,
                String.class);
        BaseResponse<MealResponse> mealResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<BaseResponse<MealResponse>>() {

                });

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(mealResponse.getData());
        assertEquals("this is fried chicken", mealResponse.getData().getDescription());

        assertTrue(mealRepository.existsById(mealResponse.getData().getId()));
    }

    @Test
    void testUpdateMeal() throws JsonMappingException, JsonProcessingException {
        EditMealDTO editMealDTO = new EditMealDTO(mealEntities.get(1).getId(), "meal 1 edited", "this is meal update",
                false);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EditMealDTO> request = new HttpEntity<EditMealDTO>(editMealDTO, headers);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/meals", HttpMethod.PUT,
                request,
                String.class);
        BaseResponse<MealResponse> mealResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<BaseResponse<MealResponse>>() {

                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(mealResponse.getData());
        assertEquals("this is meal update", mealResponse.getData().getDescription());

        MealEntity meal = mealRepository.findById(mealEntities.get(1).getId()).orElse(null);

        assertEquals("meal 1 edited", meal.getName());
        assertEquals(meal.getName(), mealResponse.getData().getName());

    }

    @Test
    void testDeleteMeal() throws JsonMappingException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + adminToken);

        HttpEntity<String> request = new HttpEntity<String>("", headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/meals/" + mealEntities.get(0).getId(), HttpMethod.DELETE, request,
                String.class);
        BaseResponse<String> mealResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<BaseResponse<String>>() {

                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(mealResponse.getData());
        assertEquals("You have successfully deleted meal", mealResponse.getData());
        assertFalse(mealRepository.existsById(mealEntities.get(0).getId()));
    }

    @Test
    void testGetMeals() throws JsonMappingException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + adminToken);

        HttpEntity<String> request = new HttpEntity<String>("", headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/meals/all", HttpMethod.GET, request,
                String.class);
        BaseResponse<List<MealResponse>> mealResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<BaseResponse<List<MealResponse>>>() {

                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(mealResponse.getData());
        assertEquals(3, mealResponse.getData().size());
    }

    @Test
    void testGetMeal() throws JsonMappingException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + adminToken);

        HttpEntity<String> request = new HttpEntity<String>("", headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/meals/" + mealEntities.get(0).getId(), HttpMethod.GET, request,
                String.class);
        BaseResponse<MealResponse> mealResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<BaseResponse<MealResponse>>() {

                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(mealResponse.getData());
        assertEquals(mealEntities.get(0).getId(), mealResponse.getData().getId());
    }

}
