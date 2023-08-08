package com.example.fooddeliverysystem.entity;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@ApiModel
@Data
@Embeddable
public class MealTypePK implements Serializable {
    @SuppressWarnings(value = "unused")
    private static final Long SerialVersionID = 1L;

    @Column(name = "meal_id")
    private int mealId;

    @Column(name = "type_entity_id")
    private int typeId;
}
