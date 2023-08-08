package com.example.fooddeliverysystem.service.meal;

import java.util.List;

import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.model.dto.AddMealDTO;
import com.example.fooddeliverysystem.model.dto.EditMealDTO;

public interface MealService {
    MealEntity editMeal(EditMealDTO mealDTO);

    MealEntity getMeal(Long id);

    boolean deleteMeal(Long id);

    List<MealEntity> getMeals();

    MealEntity insertMeal(AddMealDTO mealDTO);
}
