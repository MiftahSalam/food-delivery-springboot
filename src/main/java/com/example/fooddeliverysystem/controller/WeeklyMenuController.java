package com.example.fooddeliverysystem.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fooddeliverysystem.entity.WeeklyMenuEntity;
import com.example.fooddeliverysystem.model.dto.CreateWeeklyMenuDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.model.response.WeeklyMenuResponse;
import com.example.fooddeliverysystem.service.menu.WeeklyMenuService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("weekly-menu")
public class WeeklyMenuController {
        @Autowired
        private WeeklyMenuService weeklyMenuService;

        @CrossOrigin
        @PostMapping()
        @PreAuthorize("hasAuthority('ADMIN')")
        public ResponseEntity<BaseResponse<WeeklyMenuResponse>> saveWeeklyMenu(
                        @RequestBody @Valid CreateWeeklyMenuDTO weeklyMenuDTO) {
                WeeklyMenuEntity weeklyMenuEntity = weeklyMenuService.saveWeeklyMenu(weeklyMenuDTO);
                if (weeklyMenuEntity == null) {
                        return new ResponseEntity<>(
                                        BaseResponse.<WeeklyMenuResponse>builder()
                                                        .data(null)
                                                        .message("Fail to create weekly entity")
                                                        .status("Not created")
                                                        .build(),
                                        HttpStatus.BAD_REQUEST);
                } else {
                        return new ResponseEntity<>(
                                        BaseResponse.<WeeklyMenuResponse>builder()
                                                        .data(WeeklyMenuResponse.fromWeeklyMenuEntity(weeklyMenuEntity))
                                                        .message("Create weekly menu success")
                                                        .status("Created")
                                                        .build(),
                                        HttpStatus.CREATED);
                }
        }

        @GetMapping("/all")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<BaseResponse<Collection<WeeklyMenuResponse>>> getWeeklyMenus() {
                Collection<WeeklyMenuEntity> allWeeklyMenu = weeklyMenuService.getAllWeeklyMenu();
                if (allWeeklyMenu.isEmpty()) {
                        return new ResponseEntity<>(
                                        BaseResponse.<Collection<WeeklyMenuResponse>>builder()
                                                        .data(null)
                                                        .message("empty weekly menus")
                                                        .status("No content")
                                                        .build(),
                                        HttpStatus.NO_CONTENT);
                }

                return new ResponseEntity<>(BaseResponse.<Collection<WeeklyMenuResponse>>builder()
                                .data(WeeklyMenuResponse.fromWeeklyMenuEntities(allWeeklyMenu))
                                .message("Success to get all weekly menus")
                                .status("Ok")
                                .build(), HttpStatus.OK);
        }

        @GetMapping("/allForDailyMenu")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<BaseResponse<Collection<WeeklyMenuResponse>>> getWeeklyMenusForDaily() {
                Collection<WeeklyMenuEntity> allWeeklyMenu = weeklyMenuService.getWeeklyMenusForDailyMenu();
                if (allWeeklyMenu.isEmpty()) {
                        return new ResponseEntity<>(
                                        BaseResponse.<Collection<WeeklyMenuResponse>>builder()
                                                        .data(null)
                                                        .message("empty weekly menus")
                                                        .status("No content")
                                                        .build(),
                                        HttpStatus.NO_CONTENT);
                }

                return new ResponseEntity<>(BaseResponse.<Collection<WeeklyMenuResponse>>builder()
                                .data(WeeklyMenuResponse.fromWeeklyMenuEntities(allWeeklyMenu))
                                .message("Success to get all weekly menus")
                                .status("Ok")
                                .build(), HttpStatus.OK);
        }

        @GetMapping()
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<BaseResponse<WeeklyMenuResponse>> getWeeklyMenu() {
                WeeklyMenuEntity weeklyMenuEntity = weeklyMenuService.getCurrentWeekMenu();
                if (weeklyMenuEntity == null) {
                        return new ResponseEntity<>(
                                        BaseResponse.<WeeklyMenuResponse>builder()
                                                        .data(null)
                                                        .message("empty current weekly menu")
                                                        .status("No content")
                                                        .build(),
                                        HttpStatus.NO_CONTENT);
                }

                return new ResponseEntity<>(BaseResponse.<WeeklyMenuResponse>builder()
                                .data(WeeklyMenuResponse.fromWeeklyMenuEntity(weeklyMenuEntity))
                                .message("Success to get current weekly menu")
                                .status("Ok")
                                .build(), HttpStatus.OK);
        }
}
