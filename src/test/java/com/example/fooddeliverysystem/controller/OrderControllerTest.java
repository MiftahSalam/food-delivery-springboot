package com.example.fooddeliverysystem.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.entity.TypeEntity;
import com.example.fooddeliverysystem.model.dto.MealDTO;
import com.example.fooddeliverysystem.model.dto.MealOrderingDTO;
import com.example.fooddeliverysystem.model.dto.TypeDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.model.response.OrderResponse;
import com.example.fooddeliverysystem.repository.MealRepository;
import com.example.fooddeliverysystem.repository.MealTypeRepository;
import com.example.fooddeliverysystem.repository.TypeRepository;
import com.example.fooddeliverysystem.repository.UserOrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

public class OrderControllerTest extends BaseControllerMockMVCTest {
    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MealTypeRepository mealTypeRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private UserOrderRepository userOrderRepository;

    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @AfterEach
    void cleanUp() {
        userOrderRepository.deleteAll();
        super.cleanUp();
        mealTypeRepository.deleteAll();
        mealRepository.deleteAll();
        typeRepository.deleteAll();

    }

    @Test
    void testOrder() throws JsonProcessingException, Exception {
        List<TypeDTO> typeDTOs = new ArrayList<>();
        TypeDTO typeDTO = new TypeDTO();
        TypeDTO typeDTO1 = new TypeDTO();

        typeDTO.setName("tipe1");
        typeDTO.setPrice(10.3);
        typeDTO.setReguler(true);

        TypeEntity typeEntity = typeDTO.toEntity();
        typeEntity = typeRepository.save(typeEntity);

        typeDTO1.setName("tipe2");
        typeDTO1.setPrice(31.4);
        typeDTO1.setReguler(false);

        TypeEntity typeEntity1 = typeDTO1.toEntity();
        typeEntity1 = typeRepository.save(typeEntity1);

        typeDTOs.add(typeDTO);
        typeDTOs.add(typeDTO1);

        MealDTO mealDTO = new MealDTO();
        mealDTO.setDescription("ayam goreng dgn bumbu rempah");
        mealDTO.setEarlyOrder(true);
        mealDTO.setName("ayam rempah");
        mealDTO.setReguler(true);
        mealDTO.setTypes(typeDTOs);

        MealEntity mealEntity = mealDTO.toEntity();
        mealEntity = mealRepository.save(mealEntity);
        mealDTO.setId(mealEntity.getId());

        MealDTO mealDTO1 = new MealDTO();
        mealDTO1.setDescription("sea food dengan bumbu padang");
        mealDTO1.setEarlyOrder(true);
        mealDTO1.setName("bancakan");
        mealDTO1.setReguler(false);
        mealDTO1.setTypes(typeDTOs);

        MealEntity mealEntity1 = mealDTO1.toEntity();
        mealEntity1 = mealRepository.save(mealEntity1);
        mealDTO1.setId(mealEntity1.getId());

        typeEntity.setMeals(List.of(mealEntity));
        typeEntity1.setMeals(List.of(mealEntity1));

        typeRepository.save(typeEntity);
        typeRepository.save(typeEntity1);

        MealOrderingDTO mealOrderingDTO = new MealOrderingDTO(mealDTO, "tipe1", 3);
        MealOrderingDTO mealOrderingDTO1 = new MealOrderingDTO(mealDTO1, "tipe2", 2);

        List<MealOrderingDTO> mealOrderingDTOs = new ArrayList<MealOrderingDTO>();
        mealOrderingDTOs.add(mealOrderingDTO);
        mealOrderingDTOs.add(mealOrderingDTO1);

        mockMvc.perform(post("/orders")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mealOrderingDTOs))
                .accept(MediaType.APPLICATION_JSON)).andExpectAll(status().isOk())
                .andDo(result -> {
                    BaseResponse<List<OrderResponse>> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<BaseResponse<List<OrderResponse>>>() {

                            });

                    assertNotNull(response.getData());
                });

        mockMvc.perform(get("/orders/all")
                .param("forDay", "today")
                .header("Authorization", "Bearer " + userToken)
                .accept(MediaType.APPLICATION_JSON)).andExpectAll(status().isOk())
                .andDo(result -> {
                    // BaseResponse<List<OrderResponse>> response = objectMapper.readValue(
                    // result.getResponse().getContentAsString(),
                    // new TypeReference<BaseResponse<List<OrderResponse>>>() {

                    // });

                    // assertNotNull(response.getData());
                });
    }
}
