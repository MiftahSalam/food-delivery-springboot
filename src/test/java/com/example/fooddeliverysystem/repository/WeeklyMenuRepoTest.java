package com.example.fooddeliverysystem.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

import com.example.fooddeliverysystem.entity.WeeklyMenuEntity;

@SpringBootTest
public class WeeklyMenuRepoTest {
    @Autowired
    private WeeklyMenuRepo weeklyMenuRepo;

    @Autowired
    private TransactionOperations transactionOperations;

    @BeforeEach
    void setUp() {
        List<WeeklyMenuEntity> weeklyMenuEntities = List.of(1, 2, 3, 4).stream()
                .map(val -> generateWeeklyMenuEntity(new WeeklyMenuEntity(), val)).collect(Collectors.toList());
        // WeeklyMenuEntity weeklyMenuEntity = new WeeklyMenuEntity();
        // weeklyMenuEntity.setImagePath("week1");
        // weeklyMenuEntity.setDateFrom(Date.from(Instant.now()));
        // weeklyMenuEntity.setDateTo(Date.from(Instant.now().plus(10,
        // ChronoUnit.DAYS)));

        weeklyMenuRepo.saveAll(weeklyMenuEntities);
    }

    private WeeklyMenuEntity generateWeeklyMenuEntity(WeeklyMenuEntity weeklyMenuEntity, Integer val) {
        weeklyMenuEntity.setImagePath("Week " + val);
        weeklyMenuEntity.setDateFrom(Date.from(Instant.now()));

        weeklyMenuEntity.setDateTo(Date.from(Instant.now().plus(10 * val, ChronoUnit.DAYS)));

        return weeklyMenuEntity;
    }

    @AfterEach
    void cleanUp() {
        weeklyMenuRepo.deleteAll();
    }

    @Test
    void testfindAllByDateToAfter() {
        transactionOperations.executeWithoutResult(status -> {
            List<WeeklyMenuEntity> weeklyMenuEntities = weeklyMenuRepo
                    .findAllByDateToAfter(Date.from(Instant.now()));

            assertNull(weeklyMenuEntities);
        });
    }

    @Test
    void testFindAllByDateFromAfterAndDateToBefore() {
        List<WeeklyMenuEntity> weeklyMenuEntities = weeklyMenuRepo
                .findAllByDateFromAfterAndDateToBefore(Date.from(Instant.now()),
                        Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

        assertEquals(0, weeklyMenuEntities.size());
    }

    @Test
    void testFindAllByDateFromAfterAndDateToBefore15() {
        List<WeeklyMenuEntity> weeklyMenuEntities = weeklyMenuRepo
                .findAllByDateFromAfterAndDateToBefore(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)),
                        Date.from(Instant.now().plus(15, ChronoUnit.DAYS)));

        assertEquals(1, weeklyMenuEntities.size());
    }
}
