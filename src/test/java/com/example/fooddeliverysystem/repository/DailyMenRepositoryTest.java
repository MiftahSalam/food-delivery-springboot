package com.example.fooddeliverysystem.repository;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionOperations;

import com.example.fooddeliverysystem.entity.DailyMenuEntity;
import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.entity.WeeklyMenuEntity;

@SpringBootTest
public class DailyMenRepositoryTest {
    @Autowired
    private WeeklyMenuRepo weeklyMenuRepo;

    @Autowired
    private DailyMenRepository dailyMenRepository;

    @Autowired
    private MealRepository mealRepository;

    private final Date now = Date.from(Instant.now());

    @Autowired
    private TransactionOperations transactionOperations;

    @BeforeEach
    void setUp() {
        List<WeeklyMenuEntity> weeklyMenuEntities = List.of(1, 2, 3, 4).stream()
                .map(val -> generateWeeklyMenuEntity(new WeeklyMenuEntity(), val)).collect(Collectors.toList());

        weeklyMenuRepo.saveAll(weeklyMenuEntities);

        DailyMenuEntity dailyMenuEntity = new DailyMenuEntity();
        dailyMenuEntity.setDate(now);
        dailyMenuEntity.setWeeklyMenu(weeklyMenuEntities.get(1));

        dailyMenRepository.save(dailyMenuEntity);

        List<MealEntity> mealEntities = List.of("menu 1", "menu 2", "menu 3").stream()
                .map(name -> generateMealEntity(new MealEntity(), name)).collect(Collectors.toList());

        for (int i = 0; i < mealEntities.size(); i++) {
            mealEntities.get(i).setDailyMenus(List.of(dailyMenuEntity));
        }
        mealRepository.saveAll(mealEntities);
    }

    private WeeklyMenuEntity generateWeeklyMenuEntity(WeeklyMenuEntity weeklyMenuEntity, Integer val) {
        weeklyMenuEntity.setImagePath("Week " + val);
        weeklyMenuEntity.setDateFrom(Date.from(Instant.now()));

        weeklyMenuEntity.setDateTo(Date.from(Instant.now().plus(10 * val, ChronoUnit.DAYS)));

        return weeklyMenuEntity;
    }

    private MealEntity generateMealEntity(MealEntity mealEntity, String name) {
        mealEntity.setName(name);

        return mealEntity;
    }

    @AfterEach
    void cleanUp() {
        weeklyMenuRepo.deleteAll();
        mealRepository.deleteAll();
        dailyMenRepository.deleteAll();
    }

    @Test
    void testFindFirstByDate() {
        transactionOperations.executeWithoutResult(status -> {
            DailyMenuEntity dailyMenuEntity = dailyMenRepository.findFirstByDate(now)
                    .orElseThrow(() -> new RuntimeException("menu empty"));

            assertEquals(3, dailyMenuEntity.getMeals().size());
            assertEquals("Week 2", dailyMenuEntity.getWeeklyMenu().getImagePath());
        });
    }
}
