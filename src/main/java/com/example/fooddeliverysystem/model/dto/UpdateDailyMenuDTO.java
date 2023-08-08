package com.example.fooddeliverysystem.model.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record UpdateDailyMenuDTO(@NotNull Long dailyMenuID, @NotNull List<MealDTO> meals) {

}
