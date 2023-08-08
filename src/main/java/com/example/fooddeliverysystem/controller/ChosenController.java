package com.example.fooddeliverysystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fooddeliverysystem.model.dto.MealTypeDTO;
import com.example.fooddeliverysystem.model.dto.UserDTO;
import com.example.fooddeliverysystem.model.dto.UserIdsDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.service.user.ChosenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("chosenOne")
public class ChosenController {
    @Autowired
    private ChosenService chosenService;

    @PostMapping("setPaid")
    @PreAuthorize("hasAuthority('CHOOSEN')")
    public ResponseEntity<BaseResponse<String>> setPain(@RequestBody @Valid MealTypeDTO mealTypeDTO) {
        try {
            chosenService.setPaid(mealTypeDTO);

            return new ResponseEntity<>(
                    BaseResponse.<String>builder()
                            .data("set paid success")
                            .status("ok")
                            .message("success")
                            .build(),
                    HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    BaseResponse.<String>builder()
                            .data(null)
                            .status("failed")
                            .message("fail to set paid")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<UserDTO>> getChosen() {
        UserDTO chosen = chosenService.getChosen();
        if (chosen == null) {
            return new ResponseEntity<>(
                    BaseResponse.<UserDTO>builder()
                            .data(null)
                            .status("failed")
                            .message("fail to get chosen")
                            .build(),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
                BaseResponse.<UserDTO>builder()
                        .data(chosen)
                        .status("ok")
                        .message("success")
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping("payNotif")
    @PreAuthorize("hasAuthority('CHOOSEN')")
    public ResponseEntity<BaseResponse<String>> payNotif(@RequestBody UserIdsDTO userIdsDTO) {
        try {
            chosenService.payingNotification(userIdsDTO.ids());

            return new ResponseEntity<>(
                    BaseResponse.<String>builder()
                            .data("pay notif success")
                            .status("ok")
                            .message("success")
                            .build(),
                    HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    BaseResponse.<String>builder()
                            .data(null)
                            .status("failed")
                            .message("fail to pay notif")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

}
