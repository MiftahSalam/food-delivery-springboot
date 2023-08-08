package com.example.fooddeliverysystem.service.menu;

import java.util.Collection;

import com.example.fooddeliverysystem.entity.WeeklyMenuEntity;
import com.example.fooddeliverysystem.model.dto.CreateWeeklyMenuDTO;

public interface WeeklyMenuService {
    WeeklyMenuEntity getCurrentWeekMenu();

    WeeklyMenuEntity saveWeeklyMenu(CreateWeeklyMenuDTO weeklyMenuDTO);

    Collection<WeeklyMenuEntity> getAllWeeklyMenu();

    Collection<WeeklyMenuEntity> getWeeklyMenusForDailyMenu();
}
