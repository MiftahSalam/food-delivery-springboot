package com.example.fooddeliverysystem.model.response;

import java.util.ArrayList;
import java.util.Collection;

import com.example.fooddeliverysystem.entity.TypeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class TypeResponse {
    private Long id;

    private String name;

    private double price;

    private boolean reguler;

    public static TypeResponse fromEntity(TypeEntity typeEntity) {
        return builder()
                .id(typeEntity.getId())
                .name(typeEntity.getName())
                .price(typeEntity.getPrice())
                .reguler(typeEntity.isReguler())
                .build();
    }

    public static Collection<TypeResponse> fromTypeEntities(
            Collection<TypeEntity> typeEntities) {
        Collection<TypeResponse> typeResponses = new ArrayList<>();
        if (typeEntities != null) {
            for (TypeEntity typeEntity : typeEntities) {

                typeResponses.add(fromEntity(typeEntity));
            }
        }

        return typeResponses;
    }

}
