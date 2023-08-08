package com.example.fooddeliverysystem.service.menu;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.fooddeliverysystem.entity.DailyMenuEntity;
import com.example.fooddeliverysystem.entity.WeeklyMenuEntity;
import com.example.fooddeliverysystem.repository.DailyMenRepository;
import com.example.fooddeliverysystem.repository.MealRepository;
import com.example.fooddeliverysystem.repository.WeeklyMenuRepo;

@ExtendWith(MockitoExtension.class)
public class BaseMenuServiceTest {
    @Mock
    protected DailyMenRepository dailyMenRepository;

    @Mock
    protected WeeklyMenuRepo weeklyMenuRepo;

    @Mock
    protected MealRepository mealRepository;

    protected WeeklyMenuService weeklyMenuService;

    protected DailyMenuService dailyMenuService;

    protected List<WeeklyMenuEntity> stubMenuEntities;

    protected List<DailyMenuEntity> stubDailyMenuEntities;

    @BeforeEach
    void setUp() {
        weeklyMenuService = new WeeklyMenuServiceImpl(dailyMenRepository, weeklyMenuRepo);
        dailyMenuService = new DailyMenuServiceImpl(dailyMenRepository, weeklyMenuRepo, mealRepository);

        stubMenuEntities = List.of(-2, -1, 1, 2, 3, 4).stream()
                .map(val -> generateWeeklyMenuEntity(new WeeklyMenuEntity(), val)).collect(Collectors.toList());

        stubDailyMenuEntities = IntStream.range(1, 8)
                .mapToObj(val -> generateDailyMenuEntity(stubMenuEntities.get(2), val))
                .collect(Collectors.toList());

        stubMenuEntities.get(2).setDailyMenus(stubDailyMenuEntities);
    }

    private DailyMenuEntity generateDailyMenuEntity(WeeklyMenuEntity weeklyMenuEntity, Integer val) {
        Calendar c = Calendar.getInstance();
        Date day = c.getTime();
        c.setTime(day);
        c.add(Calendar.DATE, val);
        day = c.getTime();

        DailyMenuEntity dailyMenuEntity = new DailyMenuEntity();
        dailyMenuEntity.setDate(day);
        dailyMenuEntity.setWeeklyMenu(weeklyMenuEntity);

        return dailyMenuEntity;
    }

    private WeeklyMenuEntity generateWeeklyMenuEntity(WeeklyMenuEntity weeklyMenuEntity, Integer val) {
        weeklyMenuEntity.setImagePath("Week " + val);
        weeklyMenuEntity.setDateFrom(Date.from(Instant.now()));

        if (val < 0) {
            weeklyMenuEntity.setDateTo(Date.from(Instant.now().minus(7 * Math.abs(val), ChronoUnit.DAYS)));

        } else {
            weeklyMenuEntity.setDateTo(Date.from(Instant.now().plus(7 * val, ChronoUnit.DAYS)));
        }

        return weeklyMenuEntity;
    }

}
