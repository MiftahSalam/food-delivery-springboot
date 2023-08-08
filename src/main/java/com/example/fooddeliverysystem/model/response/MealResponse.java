package com.example.fooddeliverysystem.model.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.entity.TypeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class MealResponse {
    private Long id;

    private String name;

    private String description;

    private boolean earlyOrder;

    private boolean isReguler;

    private List<TypeResponse> types;

    public static MealResponse fromMealEntity(MealEntity mealEntity) {
        List<TypeResponse> typeResponses = new ArrayList<>();
        if (mealEntity.getTypes() != null) {
            for (TypeEntity typeEntity : mealEntity.getTypes()) {
                TypeResponse typeResponse = TypeResponse.fromEntity(typeEntity);

                typeResponses.add(typeResponse);
            }
        }
        MealResponse mealResponse = builder()
                .description(mealEntity.getDescription())
                .earlyOrder(mealEntity.isEarlyOrder())
                .id(mealEntity.getId())
                .isReguler(mealEntity.isReguler())
                .name(mealEntity.getName())
                .types(typeResponses)
                .build();

        return mealResponse;
    }

    public static Collection<MealResponse> fromMealEntities(
            Collection<MealEntity> mealEntities) {
        Collection<MealResponse> mealResponses = new ArrayList<>();
        if (mealEntities != null) {
            for (MealEntity mealEntity : mealEntities) {

                mealResponses.add(fromMealEntity(mealEntity));
            }
        }

        return mealResponses;
    }

}
