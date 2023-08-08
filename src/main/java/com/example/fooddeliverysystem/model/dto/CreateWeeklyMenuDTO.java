package com.example.fooddeliverysystem.model.dto;

import java.util.Date;

import jakarta.validation.constraints.NotNull;

public record CreateWeeklyMenuDTO(@NotNull Date fromDate, @NotNull Date toDate, @NotNull String imagePath) {

}
