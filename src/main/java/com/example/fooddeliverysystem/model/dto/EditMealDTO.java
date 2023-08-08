package com.example.fooddeliverysystem.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record EditMealDTO(
                Long id,
                @NotEmpty(message = "name is empty") String name,
                @NotEmpty(message = "description is empty") String description,
                @NotNull boolean earlyOrder) {

}
