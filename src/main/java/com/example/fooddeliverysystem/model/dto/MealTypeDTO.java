package com.example.fooddeliverysystem.model.dto;

import java.io.Serializable;

import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.entity.TypeEntity;

import lombok.Data;

@Data
public class MealTypeDTO implements Serializable {
    @SuppressWarnings(value = "unused")
    private static final long serialVersionID = 1L;

    private MealEntity mealEntity;
    private TypeEntity typeEntity;
    private Long userOrderId;
    private UserDTO user;
    private boolean paid;
}
