package com.example.fooddeliverysystem.service.menu;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.example.fooddeliverysystem.entity.DailyMenuEntity;

public class DailyMenuServiceTest extends BaseMenuServiceTest {
    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void testGetDailyMenuForToday() {
        Calendar c = Calendar.getInstance();
        Date today = new Date();
        c.setTime(today);
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();

        DailyMenuEntity todayMenu = new DailyMenuEntity();
        todayMenu.setDate(today);
        todayMenu.setWeeklyMenu(stubMenuEntities.get(2));

        when(dailyMenRepository.findFirstByDate(today)).thenReturn(Optional.of(todayMenu));
        when(dailyMenRepository.findFirstByDate(tomorrow)).thenReturn(Optional.of(stubDailyMenuEntities.get(0)));

        DailyMenuEntity dailyMenuForToday = dailyMenuService.getDailyMenuForToday();
        assertNotNull(dailyMenuForToday);
    }
}
