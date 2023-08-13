package com.example.fooddeliverysystem.controller;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fooddeliverysystem.entity.DailyMenuEntity;
import com.example.fooddeliverysystem.model.dto.DailyMenuDTO;
import com.example.fooddeliverysystem.model.dto.UpdateDailyMenuDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.model.response.DailyMenuResponse;
import com.example.fooddeliverysystem.service.menu.DailyMenuService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/daily-menu")
public class DailyMenuController {
    @Autowired
    private DailyMenuService dailyMenuService;

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<DailyMenuResponse>> getDailyMenu() {
        DailyMenuEntity dailyMenuForToday = dailyMenuService.getDailyMenuForToday();
        if (dailyMenuForToday == null) {
            return new ResponseEntity<>(BaseResponse.<DailyMenuResponse>builder()
                    .data(null)
                    .message("Empty daily menu")
                    .status("No content")
                    .build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(BaseResponse.<DailyMenuResponse>builder()
                    .data(DailyMenuResponse.fromDailyMenuEntity(dailyMenuForToday))
                    .message("Success to get daily menu")
                    .status("ok")
                    .build(), HttpStatus.OK);
        }
    }

    @CrossOrigin
    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse<DailyMenuResponse>> saveDailyMenu(
            @Valid @RequestBody DailyMenuDTO dailyMenuDTO) {
        DailyMenuEntity saveDailyMenu = dailyMenuService.saveDailyMenu(dailyMenuDTO);
        if (saveDailyMenu == null) {
            return new ResponseEntity<>(BaseResponse.<DailyMenuResponse>builder()
                    .data(null)
                    .message("Failed to create daily menu")
                    .status("failed")
                    .build(), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(BaseResponse.<DailyMenuResponse>builder()
                    .data(DailyMenuResponse.fromDailyMenuEntity(saveDailyMenu))
                    .message("Success to create daily menu")
                    .status("ok")
                    .build(), HttpStatus.OK);

        }
    }

    @CrossOrigin
    @PutMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse<DailyMenuResponse>> updateDailyMenu(
            @Valid @RequestBody UpdateDailyMenuDTO dailyMenuDTO) {
        DailyMenuEntity updatedDailyMenu = dailyMenuService.updateDailyMenu(dailyMenuDTO);
        if (updatedDailyMenu == null) {
            return new ResponseEntity<>(BaseResponse.<DailyMenuResponse>builder()
                    .data(null)
                    .message("Failed to update daily menu")
                    .status("failed")
                    .build(), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(BaseResponse.<DailyMenuResponse>builder()
                    .data(DailyMenuResponse.fromDailyMenuEntity(updatedDailyMenu))
                    .message("Success to create daily menu")
                    .status("ok")
                    .build(), HttpStatus.OK);

        }
    }

    @GetMapping("/all/{weeklyMenuId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Collection<DailyMenuResponse>>> getDailyMenu(
            @PathVariable("weeklyMenuId") Long weeklyMenuId) {
        Collection<DailyMenuEntity> dailyMenus = dailyMenuService.getDailyMenus(weeklyMenuId);
        if (dailyMenus != null && !dailyMenus.isEmpty()) {
            return new ResponseEntity<>(BaseResponse.<Collection<DailyMenuResponse>>builder()
                    .message("Success get daily menus")
                    .status("ok")
                    .data(DailyMenuResponse.fromDailyMenuEntities(dailyMenus))
                    .build(), HttpStatus.OK);
        }

        return new ResponseEntity<>(BaseResponse.<Collection<DailyMenuResponse>>builder()
                .data(null)
                .message("Empty daily menus")
                .status("no content")
                .build(), HttpStatus.NO_CONTENT);
    }

    @GetMapping("/days/{weeklyMenuId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Collection<Date>>> getDays(
            @PathVariable("weeklyMenuId") Long weeklyMenuId) {
        Collection<Date> dates = dailyMenuService.getDays(weeklyMenuId);
        if (!dates.isEmpty()) {
            return new ResponseEntity<>(BaseResponse.<Collection<Date>>builder()
                    .message("Success get daily menus")
                    .status("ok")
                    .data(dates)
                    .build(), HttpStatus.OK);
        }

        return new ResponseEntity<>(BaseResponse.<Collection<Date>>builder()
                .data(null)
                .message("Empty daily menus")
                .status("no content")
                .build(), HttpStatus.NO_CONTENT);
    }
}
