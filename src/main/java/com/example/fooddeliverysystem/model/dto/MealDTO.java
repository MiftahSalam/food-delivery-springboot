package com.example.fooddeliverysystem.model.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.entity.TypeEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MealDTO implements BaseDTO<MealEntity> {
    @NotNull
    private Long id;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private boolean earlyOrder;

    @NotNull
    private boolean isReguler;

    private List<TypeDTO> types;

    @Override
    public MealEntity toEntity() {
        List<TypeEntity> typeEntities = new ArrayList<>();

        MealEntity mealEntity = new MealEntity();
        mealEntity.setDescription(description);
        mealEntity.setEarlyOrder(earlyOrder);
        mealEntity.setName(name);
        mealEntity.setReguler(isReguler);

        if (types != null) {
            for (TypeDTO typeDTO : types) {
                typeEntities.add(typeDTO.toEntity());
            }

        }
        mealEntity.setTypes(typeEntities);

        return mealEntity;
    }

}