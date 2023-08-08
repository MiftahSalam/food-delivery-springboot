package com.example.fooddeliverysystem.model.dto;

import com.example.fooddeliverysystem.entity.TypeEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TypeDTO implements BaseDTO<TypeEntity> {
    @NotBlank
    private String name;

    @Positive
    private double price;

    private boolean reguler;

    @Override
    public TypeEntity toEntity() {
        TypeEntity typeEntity = new TypeEntity();
        typeEntity.setName(name);
        typeEntity.setPrice(price);
        typeEntity.setReguler(reguler);

        return typeEntity;
    }

}
