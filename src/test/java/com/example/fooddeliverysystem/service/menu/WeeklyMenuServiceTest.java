package com.example.fooddeliverysystem.service.menu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.example.fooddeliverysystem.entity.DailyMenuEntity;
import com.example.fooddeliverysystem.entity.WeeklyMenuEntity;
import com.example.fooddeliverysystem.model.dto.CreateWeeklyMenuDTO;

public class WeeklyMenuServiceTest extends BaseMenuServiceTest {
    @Test
    void testGetCurrentWeekMenu() {
        when(weeklyMenuRepo.findAll()).thenReturn(stubMenuEntities);

        WeeklyMenuEntity currentWeekMenu = weeklyMenuService.getCurrentWeekMenu();
        assertEquals("Week 1", currentWeekMenu.getImagePath());

        verify(weeklyMenuRepo, times(1)).findAll();
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void testSaveWeeklyMenuSuccess() {
        Date fromDate = Date.from(Instant.now().plus(Period.ofWeeks(5)));
        Date toDate = Date.from(Instant.now().plus(Period.ofWeeks(6)));
        CreateWeeklyMenuDTO weeklyMenuDTO = new CreateWeeklyMenuDTO(fromDate, toDate, "test menu week");

        WeeklyMenuEntity createdWeeklyMenu = new WeeklyMenuEntity();
        createdWeeklyMenu.setDateFrom(weeklyMenuDTO.fromDate());
        createdWeeklyMenu.setDateTo(weeklyMenuDTO.toDate());
        createdWeeklyMenu.setImagePath(weeklyMenuDTO.imagePath());
        createdWeeklyMenu.setDailyMenus(stubDailyMenuEntities);

        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.setTime(weeklyMenuDTO.fromDate());
        calendarFrom.add(Calendar.DATE, 1);

        Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTime(weeklyMenuDTO.toDate());
        calendarTo.add(Calendar.DATE, 1);

        when(weeklyMenuRepo.findAllByDateFromAfterAndDateToBefore(calendarFrom.getTime(), calendarTo.getTime()))
                .thenReturn(null);
        when(weeklyMenuRepo.findAll()).thenReturn(stubMenuEntities);
        for (DailyMenuEntity dailyMenuEntity : stubDailyMenuEntities) {
            when(dailyMenRepository.save(dailyMenuEntity)).thenReturn(dailyMenuEntity);
        }
        when(weeklyMenuRepo.save(any(WeeklyMenuEntity.class))).thenReturn(createdWeeklyMenu);

        WeeklyMenuEntity saveWeeklyMenu = weeklyMenuService.saveWeeklyMenu(weeklyMenuDTO);
        assertEquals("test menu week", saveWeeklyMenu.getImagePath());
        assertEquals(7, saveWeeklyMenu.getDailyMenus().size());
        assertEquals(weeklyMenuDTO.fromDate(), saveWeeklyMenu.getDateFrom());

    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void testSaveWeeklyMenuFailed() {
        Date fromDate = Date.from(Instant.now().plus(Period.ofWeeks(2)));
        Date toDate = Date.from(Instant.now().plus(Period.ofWeeks(3)));
        CreateWeeklyMenuDTO weeklyMenuDTO = new CreateWeeklyMenuDTO(fromDate, toDate, "test menu week");

        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.setTime(weeklyMenuDTO.fromDate());
        calendarFrom.add(Calendar.DATE, 1);

        Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTime(weeklyMenuDTO.toDate());
        calendarTo.add(Calendar.DATE, 1);

        when(weeklyMenuRepo.findAllByDateFromAfterAndDateToBefore(calendarFrom.getTime(), calendarTo.getTime()))
                .thenReturn(null);
        when(weeklyMenuRepo.findAll()).thenReturn(stubMenuEntities);
        for (DailyMenuEntity dailyMenuEntity : stubDailyMenuEntities) {
            when(dailyMenRepository.save(dailyMenuEntity)).thenReturn(dailyMenuEntity);
        }

        WeeklyMenuEntity saveWeeklyMenu = weeklyMenuService.saveWeeklyMenu(weeklyMenuDTO);
        assertNull(saveWeeklyMenu);
    }
}
