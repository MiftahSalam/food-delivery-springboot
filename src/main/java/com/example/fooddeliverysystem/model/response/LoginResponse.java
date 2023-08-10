package com.example.fooddeliverysystem.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginResponse {
    private int userid;

    private String email;

    private String name;

    private String token;

    private String lastName;

    private String imagePath;

    private int status;

    private String role;
}
