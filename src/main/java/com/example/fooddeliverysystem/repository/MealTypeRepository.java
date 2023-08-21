package com.example.fooddeliverysystem.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.entity.MealTypeEntity;
import com.example.fooddeliverysystem.entity.MealTypePK;
import com.example.fooddeliverysystem.entity.TypeEntity;

public interface MealTypeRepository extends JpaRepository<MealTypeEntity, Long> {
    // public interface MealTypeRepository extends JpaRepository<MealTypeEntity,
    // MealTypePK> {
    // Optional<MealTypeEntity> findById(MealTypePK id);

    Collection<MealTypeEntity> findAllByTypeEntityId(Long typeId);

    MealTypeEntity findByMealIdAndTypeEntityId(Long mealId, Long typeId);

    void deleteByTypeEntityId(Long typeId);
}
