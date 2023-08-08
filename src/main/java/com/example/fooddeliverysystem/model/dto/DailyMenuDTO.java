package com.example.fooddeliverysystem.model.dto;

import java.util.Date;
import java.util.List;

public record DailyMenuDTO(Date date, List<MealDTO> meals, WeeklyMenuDTO weeklyMenu) {

}
