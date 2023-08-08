package com.example.fooddeliverysystem.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.example.fooddeliverysystem.entity.WeeklyMenuEntity;
import com.example.fooddeliverysystem.model.dto.CreateWeeklyMenuDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.model.response.WeeklyMenuResponse;
import com.example.fooddeliverysystem.repository.WeeklyMenuRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class WeeklyMenuControllerTest extends BaseControllerMockMVCTest {
        @Autowired
        private WeeklyMenuRepo weeklyMenuRepo;

        @BeforeEach
        void setUp() {
                super.setUp();
        }

        private WeeklyMenuEntity generateWeeklyMenuEntity(WeeklyMenuEntity weeklyMenuEntity, Integer val) {
                weeklyMenuEntity.setImagePath("Week " + val);
                weeklyMenuEntity.setDateFrom(Date.from(Instant.now()));

                if (val < 0) {
                        weeklyMenuEntity.setDateTo(Date.from(Instant.now().minus(7 * Math.abs(val), ChronoUnit.DAYS)));

                } else {
                        weeklyMenuEntity.setDateTo(Date.from(Instant.now().plus(7 * val, ChronoUnit.DAYS)));
                }

                return weeklyMenuEntity;
        }

        @AfterEach
        void cleanUp() {
                weeklyMenuRepo.deleteAll();
                super.cleanUp();
        }

        @Test
        void testSaveWeeklyMenuSuccess() throws JsonProcessingException, Exception {
                Date fromDate = Date.from(Instant.now());
                Date toDate = Date.from(Instant.now().plus(Period.ofDays(4)));
                CreateWeeklyMenuDTO weeklyMenuDTO = new CreateWeeklyMenuDTO(fromDate, toDate,
                                "http://test.image.com/img1.png");

                System.out.println(objectMapper.writeValueAsString(weeklyMenuDTO));
                mockMvc.perform(post("/weekly-menu")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + adminToken)
                                .content(objectMapper.writeValueAsString(weeklyMenuDTO))
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpectAll(
                                                status().isCreated())
                                .andDo(result -> {
                                        BaseResponse<WeeklyMenuResponse> weeklyMenuResponse = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<BaseResponse<WeeklyMenuResponse>>() {

                                                        });

                                        assertNotNull(weeklyMenuResponse);
                                        assertEquals(Date.from(toDate.toInstant().plus(Period.ofDays(1))),
                                                        weeklyMenuResponse.getData().getDateTo());
                                        assertEquals(Date.from(fromDate.toInstant().plus(Period.ofDays(1))),
                                                        weeklyMenuResponse.getData().getDateFrom());
                                        assertEquals(weeklyMenuDTO.imagePath(),
                                                        weeklyMenuResponse.getData().getImagePath());

                                        assertTrue(weeklyMenuRepo.existsById(weeklyMenuResponse.getData().getId()));
                                });
        }

        @Test
        void testSaveWeeklyMenuForbidden() throws JsonProcessingException, Exception {
                Date fromDate = Date.from(Instant.now());
                Date toDate = Date.from(Instant.now().plus(Period.ofDays(4)));
                CreateWeeklyMenuDTO weeklyMenuDTO = new CreateWeeklyMenuDTO(fromDate, toDate,
                                "http://test.image.com/img1.png");

                System.out.println(objectMapper.writeValueAsString(weeklyMenuDTO));
                mockMvc.perform(post("/weekly-menu")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + userToken)
                                .content(objectMapper.writeValueAsString(weeklyMenuDTO))
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpectAll(
                                                status().isForbidden());
        }

        @Test
        void testGetAllWeeklyMenuSuccess() throws JsonProcessingException, Exception {
                List<WeeklyMenuEntity> weeklyMenuEntities = List.of(1, 2, 3, 4).stream()
                                .map(val -> generateWeeklyMenuEntity(new WeeklyMenuEntity(), val))
                                .collect(Collectors.toList());

                weeklyMenuRepo.saveAll(weeklyMenuEntities);

                mockMvc.perform(get("/weekly-menu/all")
                                .header("Authorization", "Bearer " + adminToken)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        BaseResponse<List<WeeklyMenuResponse>> weeklyMenuResponses = objectMapper
                                                        .readValue(
                                                                        result.getResponse().getContentAsString(),
                                                                        new TypeReference<BaseResponse<List<WeeklyMenuResponse>>>() {

                                                                        });

                                        assertNotNull(weeklyMenuResponses);
                                        assertEquals(4, weeklyMenuResponses.getData().size());

                                });

                mockMvc.perform(get("/weekly-menu/all")
                                .header("Authorization", "Bearer " + userToken)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        BaseResponse<List<WeeklyMenuResponse>> weeklyMenuResponses = objectMapper
                                                        .readValue(
                                                                        result.getResponse().getContentAsString(),
                                                                        new TypeReference<BaseResponse<List<WeeklyMenuResponse>>>() {

                                                                        });

                                        assertNotNull(weeklyMenuResponses);
                                        assertEquals(4, weeklyMenuResponses.getData().size());

                                });
        }

        @Test
        void testGetAllWeeklyMenuNotAuthecticate() throws JsonProcessingException, Exception {
                List<WeeklyMenuEntity> weeklyMenuEntities = List.of(1, 2, 3, 4).stream()
                                .map(val -> generateWeeklyMenuEntity(new WeeklyMenuEntity(), val))
                                .collect(Collectors.toList());

                weeklyMenuRepo.saveAll(weeklyMenuEntities);

                mockMvc.perform(get("/weekly-menu/all")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpectAll(
                                                status().isForbidden());

        }

        @Test
        void testGetWeeklyMenuForDailySuccess() throws JsonProcessingException, Exception {
                List<WeeklyMenuEntity> weeklyMenuEntities = List.of(-2, -1, 1, 2, 3).stream()
                                .map(val -> generateWeeklyMenuEntity(new WeeklyMenuEntity(), val))
                                .collect(Collectors.toList());

                weeklyMenuRepo.saveAll(weeklyMenuEntities);

                mockMvc.perform(get("/weekly-menu/allForDailyMenu")
                                .header("Authorization", "Bearer " + adminToken)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        BaseResponse<List<WeeklyMenuResponse>> weeklyMenuResponses = objectMapper
                                                        .readValue(
                                                                        result.getResponse().getContentAsString(),
                                                                        new TypeReference<BaseResponse<List<WeeklyMenuResponse>>>() {

                                                                        });

                                        assertNotNull(weeklyMenuResponses);
                                        assertEquals(3, weeklyMenuResponses.getData().size());

                                });

        }

        @Test
        void testGetCurrentWeeklyMenuSuccess() throws JsonProcessingException, Exception {
                List<WeeklyMenuEntity> weeklyMenuEntities = List.of(-2, -1, 1, 2, 3).stream()
                                .map(val -> generateWeeklyMenuEntity(new WeeklyMenuEntity(), val))
                                .collect(Collectors.toList());

                List<WeeklyMenuEntity> savedWeeklyMenuEntities = weeklyMenuRepo.saveAll(weeklyMenuEntities);
                assertEquals(5, savedWeeklyMenuEntities.size());

                mockMvc.perform(get("/weekly-menu")
                                .header("Authorization", "Bearer " + adminToken)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        BaseResponse<WeeklyMenuResponse> weeklyMenuResponse = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<BaseResponse<WeeklyMenuResponse>>() {

                                                        });

                                        assertNotNull(weeklyMenuResponse);
                                        assertEquals(savedWeeklyMenuEntities.get(2).getId(),
                                                        weeklyMenuResponse.getData().getId());
                                });

        }
}
