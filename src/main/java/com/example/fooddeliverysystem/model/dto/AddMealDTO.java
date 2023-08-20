package com.example.fooddeliverysystem.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AddMealDTO(
                @NotBlank String name,
                String description,
                @NotNull boolean earlyOrder,
                @NotNull boolean isReguler,
                @JsonProperty("types") @NotNull() @NotEmpty() List<TypeDTO> typeEntities) {

}
