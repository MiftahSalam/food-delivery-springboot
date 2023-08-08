package com.example.fooddeliverysystem.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MealOrderingDTO(@NotNull MealDTO mealDTO, @NotEmpty(message = "Regular is empty") String regular,
        @Positive(message = "count isn't positive number") int count) {

}
