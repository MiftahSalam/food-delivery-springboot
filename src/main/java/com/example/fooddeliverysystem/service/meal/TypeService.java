package com.example.fooddeliverysystem.service.meal;

import java.util.Collection;
import java.util.List;

import com.example.fooddeliverysystem.entity.TypeEntity;
import com.example.fooddeliverysystem.model.dto.UpdateTypeDTO;

public interface TypeService {
    TypeEntity getType(Long id);

    Collection<TypeEntity> getAllTypes();

    boolean deleteType(Long id);

    TypeEntity insertType(TypeEntity typeEntity);

    TypeEntity updateType(UpdateTypeDTO typeEntity);

    Collection<TypeEntity> getRegularTypes();

    Collection<TypeEntity> getTypesWithRegular();

    Collection<TypeEntity> getTypesWithoutRegular();

    Collection<TypeEntity> updateRegularTypes(List<TypeEntity> typeEntities);

}
