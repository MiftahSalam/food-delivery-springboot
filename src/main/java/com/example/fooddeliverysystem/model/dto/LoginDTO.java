package com.example.fooddeliverysystem.model.dto;

import jakarta.validation.constraints.NotEmpty;

public record LoginDTO(@NotEmpty(message = "email address is empty") String email,
        @NotEmpty(message = "password is empty") String password) {

}
