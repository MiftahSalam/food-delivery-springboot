package com.example.fooddeliverysystem.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.model.dto.AddMealDTO;
import com.example.fooddeliverysystem.model.dto.EditMealDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.model.response.MealResponse;
import com.example.fooddeliverysystem.service.meal.MealService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("meals")
public class MealController {
    @Autowired
    private MealService mealService;

    @CrossOrigin
    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<MealResponse>> addMeal(@RequestBody @Valid AddMealDTO mealDTO) {
        MealEntity insertMeal = mealService.insertMeal(mealDTO);
        if (insertMeal != null) {
            return new ResponseEntity<>(BaseResponse.<MealResponse>builder()
                    .data(MealResponse.fromMealEntity(insertMeal))
                    .message("success to add meal")
                    .status("ok")
                    .build(), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(BaseResponse.<MealResponse>builder()
                .data(null)
                .message("failed to add typmeale")
                .status("not modified")
                .build(), HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin
    @PutMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse<MealResponse>> updateMeal(@RequestBody @Valid EditMealDTO mealDTO) {
        MealEntity updatedeMeal = mealService.editMeal(mealDTO);
        if (updatedeMeal != null) {
            return new ResponseEntity<>(BaseResponse.<MealResponse>builder()
                    .data(MealResponse.fromMealEntity(updatedeMeal))
                    .message("success to update meal")
                    .status("ok")
                    .build(), HttpStatus.OK);
        }

        return new ResponseEntity<>(BaseResponse.<MealResponse>builder()
                .data(null)
                .message("failed to add typmeale")
                .status("not modified")
                .build(), HttpStatus.NOT_MODIFIED);
    }

    @CrossOrigin
    @DeleteMapping("/{mealId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse<String>> deleteMeal(@PathVariable("mealId") Long mealId) {
        if (mealService.deleteMeal(mealId)) {
            return new ResponseEntity<>(BaseResponse.<String>builder()
                    .data("You have successfully deleted meal")
                    .message("success")
                    .status("ok")
                    .build(),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(BaseResponse.<String>builder()
                    .data("Error occuring when deleting meal")
                    .message("failed")
                    .status("failed")
                    .build(),
                    HttpStatus.BAD_REQUEST);

        }
    }

    @GetMapping(value = "/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Collection<MealResponse>>> getMeals() {
        List<MealEntity> meals = mealService.getMeals();
        if (meals.isEmpty()) {
            return new ResponseEntity<>(BaseResponse.<Collection<MealResponse>>builder()
                    .data(null)
                    .status("empty")
                    .message("empty content")
                    .build(), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(BaseResponse.<Collection<MealResponse>>builder()
                .data(MealResponse.fromMealEntities(meals))
                .status("success to get all meals")
                .message("ok")
                .build(), HttpStatus.OK);
    }

    @GetMapping("/{mealId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<MealResponse>> getMeal(@PathVariable("mealId") Long mealId) {
        MealEntity meal = mealService.getMeal(mealId);
        if (meal == null) {
            return new ResponseEntity<>(BaseResponse.<MealResponse>builder()
                    .data(null)
                    .status("empty")
                    .message("empty content")
                    .build(), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(BaseResponse.<MealResponse>builder()
                .data(MealResponse.fromMealEntity(meal))
                .status("success to get meal")
                .message("ok")
                .build(), HttpStatus.OK);
    }

}
