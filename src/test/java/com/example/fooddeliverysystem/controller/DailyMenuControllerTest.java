package com.example.fooddeliverysystem.controller;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.example.fooddeliverysystem.entity.DailyMenuEntity;
import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.entity.WeeklyMenuEntity;
import com.example.fooddeliverysystem.model.dto.DailyMenuDTO;
import com.example.fooddeliverysystem.model.dto.MealDTO;
import com.example.fooddeliverysystem.model.dto.UpdateDailyMenuDTO;
import com.example.fooddeliverysystem.model.dto.WeeklyMenuDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.model.response.DailyMenuResponse;
import com.example.fooddeliverysystem.repository.DailyMenRepository;
import com.example.fooddeliverysystem.repository.MealRepository;
import com.example.fooddeliverysystem.repository.WeeklyMenuRepo;
import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DailyMenuControllerTest extends BaseControllerMockMVCTest {
    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private DailyMenRepository dailyMenRepository;

    @Autowired
    private WeeklyMenuRepo weeklyMenuRepo;

    private final Date now = Date.from(Instant.now());

    private List<DailyMenuEntity> dailyMenuEntities = new ArrayList<>();

    private WeeklyMenuEntity weeklyMenuEntity;

    @BeforeEach
    void setUp() {
        super.setUp();

        weeklyMenuEntity = new WeeklyMenuEntity();
        weeklyMenuEntity.setImagePath("https://fds.com/week1.png");
        weeklyMenuEntity.setDateFrom(Date.from(Instant.now()));
        weeklyMenuEntity.setDateTo(Date.from(Instant.now().plus(Period.ofDays(10))));

        weeklyMenuRepo.save(weeklyMenuEntity);

        dailyMenuEntities.clear();
        dailyMenuEntities = List.of(0, 1, 2, 3, 4, 5).stream()
                .map(val -> generateDailyMenuEntity(weeklyMenuEntity, new DailyMenuEntity(), val))
                .collect(Collectors.toList());

        dailyMenRepository.saveAll(dailyMenuEntities);

        for (int j = 0; j < 3; j++) {
            MealEntity mealEntity = new MealEntity();
            mealEntity.setName("menu " + j);
            mealEntity.setDailyMenus(dailyMenuEntities);

            mealRepository.save(mealEntity);
        }
    }

    private DailyMenuEntity generateDailyMenuEntity(WeeklyMenuEntity weeklyMenuEntity, DailyMenuEntity dailyMenuEntity,
            Integer val) {
        dailyMenuEntity.setDate(Date.from(now.toInstant().plus(Period.ofDays(val))));
        dailyMenuEntity.setWeeklyMenu(weeklyMenuEntity);

        return dailyMenuEntity;
    }

    @AfterEach
    void cleanUp() {
        mealRepository.deleteAll();
        dailyMenRepository.deleteAll();
        weeklyMenuRepo.deleteAll();
        super.cleanUp();
    }

    @Test
    void testGetDailyMenu() throws Exception {
        mockMvc.perform(get("/daily-menu")
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON)).andExpectAll(status().isOk())
                .andDo(result -> {
                    BaseResponse<DailyMenuResponse> dailyResponse = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<BaseResponse<DailyMenuResponse>>() {

                            });

                    assertNotNull(dailyResponse);
                });
    }

    @Test
    void testGetDailyInAWeek() throws Exception {
        mockMvc.perform(get("/daily-menu/all/" + weeklyMenuEntity.getId())
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON)).andExpectAll(status().isOk())
                .andDo(result -> {
                    BaseResponse<List<DailyMenuResponse>> dailyResponse = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<BaseResponse<List<DailyMenuResponse>>>() {

                            });

                    assertNotNull(dailyResponse);
                    assertEquals(6, dailyResponse.getData().size());
                });
    }

    @Test
    void testGetDaysInAWeek() throws Exception {
        mockMvc.perform(get("/daily-menu/days/" + weeklyMenuEntity.getId())
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON)).andExpectAll(status().isOk())
                .andDo(result -> {
                    BaseResponse<List<Date>> dailyResponse = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<BaseResponse<List<Date>>>() {

                            });

                    assertNotNull(dailyResponse);
                    assertEquals(5, dailyResponse.getData().size());
                });
    }

    @Test
    void testCreateDailyMenu() throws Exception {
        List<WeeklyMenuDTO> weeklyMenuDTOs = new ArrayList<>();
        weeklyMenuRepo.findAll().forEach(weeklyMenu -> {
            WeeklyMenuDTO weeklyMenuDTO = new WeeklyMenuDTO(weeklyMenu.getId(), weeklyMenu.getDateFrom(),
                    weeklyMenu.getDateTo(), weeklyMenu.getImagePath());

            weeklyMenuDTOs.add(weeklyMenuDTO);
        });
        assertTrue("should return weekly menu with size 1", 1 == weeklyMenuDTOs.size());

        List<MealDTO> mealDTOs = new ArrayList<>();
        mealRepository.findAll().forEach(meal -> {
            MealDTO mealDTO = new MealDTO();
            mealDTO.setDescription(meal.getDescription());
            mealDTO.setEarlyOrder(meal.isEarlyOrder());
            mealDTO.setId(meal.getId());
            mealDTO.setName(meal.getName());
            mealDTO.setReguler(meal.isReguler());
            mealDTO.setTypes(null);

            mealDTOs.add(mealDTO);
        });
        assertTrue("should return weekly menu with size more than 1", 2 < mealDTOs.size());

        DailyMenuDTO dailyMenuDTO = new DailyMenuDTO(Date.from(now.toInstant().plus(Period.ofDays(7))), mealDTOs,
                weeklyMenuDTOs.get(0));

        mockMvc.perform(post("/daily-menu")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dailyMenuDTO))
                .accept(MediaType.APPLICATION_JSON)).andExpectAll(status().isOk())
                .andDo(result -> {
                    BaseResponse<DailyMenuResponse> dailyResponse = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<BaseResponse<DailyMenuResponse>>() {

                            });

                    assertNotNull(dailyResponse);
                    assertEquals(Date.from(now.toInstant().plus(Period.ofDays(7))), dailyResponse.getData().getDate());
                    assertEquals(3, dailyResponse.getData().getMeals().size());
                });
    }

    @Test
    void testUpdateDailyMenu() throws Exception {
        List<MealDTO> mealDTOs = new ArrayList<>();
        mealRepository.findAll().subList(0, 1).forEach(meal -> {
            MealDTO mealDTO = new MealDTO();
            mealDTO.setDescription(meal.getDescription());
            mealDTO.setEarlyOrder(meal.isEarlyOrder());
            mealDTO.setId(meal.getId());
            mealDTO.setName(meal.getName());
            mealDTO.setReguler(meal.isReguler());
            mealDTO.setTypes(null);

            mealDTOs.add(mealDTO);
        });

        UpdateDailyMenuDTO dailyMenuDTO = new UpdateDailyMenuDTO(dailyMenuEntities.get(2).getId(), mealDTOs);

        mockMvc.perform(put("/daily-menu")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dailyMenuDTO))
                .accept(MediaType.APPLICATION_JSON)).andExpectAll(status().isOk())
                .andDo(result -> {
                    BaseResponse<DailyMenuResponse> dailyResponse = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<BaseResponse<DailyMenuResponse>>() {

                            });

                    assertNotNull(dailyResponse);
                    assertEquals(dailyMenuEntities.get(2).getId(), dailyResponse.getData().getId());
                    assertEquals(1, dailyResponse.getData().getMeals().size());
                });
    }
}
