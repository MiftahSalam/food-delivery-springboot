package com.example.fooddeliverysystem.model.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRegisterDTO(
                @NotBlank String email,
                @NotBlank String password,
                @NotBlank String name,
                String lastName,
                String imagePath) {

}
