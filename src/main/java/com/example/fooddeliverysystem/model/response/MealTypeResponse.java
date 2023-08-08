package com.example.fooddeliverysystem.model.response;

import com.example.fooddeliverysystem.entity.MealTypeEntity;
import com.example.fooddeliverysystem.entity.MealTypePK;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MealTypeResponse {
    private MealTypePK id;

    private MealResponse mealResponse;

    private TypeResponse typeResponse;

    public static MealTypeResponse fromEntity(MealTypeEntity mealTypeEntity) {
        MealTypeResponse mealTypeResponse = new MealTypeResponse();
        mealTypeResponse.setId(mealTypeEntity.getId());
        mealTypeResponse.setMealResponse(MealResponse.fromMealEntity(mealTypeEntity.getMeal()));
        mealTypeResponse.setTypeResponse(TypeResponse.fromEntity(mealTypeEntity.getTypeEntity()));

        return mealTypeResponse;
    }
}
