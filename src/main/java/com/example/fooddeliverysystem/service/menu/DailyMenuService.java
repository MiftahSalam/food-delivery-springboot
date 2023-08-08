package com.example.fooddeliverysystem.service.menu;

import java.util.Collection;
import java.util.Date;

import com.example.fooddeliverysystem.entity.DailyMenuEntity;
import com.example.fooddeliverysystem.model.dto.DailyMenuDTO;
import com.example.fooddeliverysystem.model.dto.UpdateDailyMenuDTO;

public interface DailyMenuService {
    DailyMenuEntity getDailyMenuForToday();

    Collection<DailyMenuEntity> getDailyMenus(Long weeklyMenuId);

    DailyMenuEntity saveDailyMenu(DailyMenuDTO menu);

    DailyMenuEntity updateDailyMenu(UpdateDailyMenuDTO menu);

    Collection<Date> getDays(Long weeklyMenuId);
}
