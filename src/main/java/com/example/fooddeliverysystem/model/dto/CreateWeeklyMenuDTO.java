package com.example.fooddeliverysystem.model.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public record CreateWeeklyMenuDTO(
        @NotNull @JsonProperty(value = "from") Date fromDate,
        @NotNull @JsonProperty(value = "to") Date toDate,
        @NotNull String imagePath) {

}
