package com.example.fooddeliverysystem.model.dto;

import java.util.Date;

import jakarta.validation.constraints.NotNull;

public record WeeklyMenuDTO(@NotNull Long id, @NotNull Date fromDate, @NotNull Date toDate, @NotNull String imagePath) {

}
