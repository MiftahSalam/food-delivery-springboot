package com.example.fooddeliverysystem.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateTypeDTO(@NotNull Long id, @NotBlank String name, @Positive double price, boolean reguler) {

}
