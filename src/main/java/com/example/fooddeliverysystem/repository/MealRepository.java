package com.example.fooddeliverysystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fooddeliverysystem.entity.MealEntity;

public interface MealRepository extends JpaRepository<MealEntity, Long> {

}
