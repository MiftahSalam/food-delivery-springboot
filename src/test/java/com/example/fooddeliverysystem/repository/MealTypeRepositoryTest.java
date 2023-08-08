package com.example.fooddeliverysystem.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.entity.MealTypeEntity;
import com.example.fooddeliverysystem.entity.MealTypePK;
import com.example.fooddeliverysystem.entity.TypeEntity;

@SpringBootTest
public class MealTypeRepositoryTest {
    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private MealTypeRepository mealTypeRepository;

    private MealTypePK mealTypePK;

    @BeforeEach
    void setUp() {
        MealEntity mealEntity = new MealEntity();
        mealEntity.setName("meal 1");
        mealRepository.save(mealEntity);

        TypeEntity typeEntity = new TypeEntity();
        typeEntity.setName("type test");
        typeEntity.setPrice(10.32);
        typeEntity.setMeals(List.of(mealEntity));

        typeRepository.save(typeEntity);

        mealTypePK = new MealTypePK();
        mealTypePK.setMealId(mealEntity.getId().intValue());
        mealTypePK.setTypeId(typeEntity.getId().intValue());

        MealTypeEntity mealTypeEntity = new MealTypeEntity();
        mealTypeEntity.setId(mealTypePK);
        mealTypeEntity.setMeal(mealEntity);
        mealTypeEntity.setTypeEntity(typeEntity);

        mealTypeRepository.save(mealTypeEntity);
    }

    @AfterEach
    void cleanUp() {
        assertDoesNotThrow(() -> {
            mealTypeRepository.deleteAll();
            mealRepository.deleteAll();
            typeRepository.deleteAll();

        }, "clean up should not error");
    }

    @Test
    void testFindById() {
        MealTypeEntity mealTypeEntity = mealTypeRepository.findById(mealTypePK)
                .orElseThrow(() -> new RuntimeException("mealtype empty"));

        assertEquals("meal 1", mealTypeEntity.getMeal().getName());
        assertEquals("type test", mealTypeEntity.getTypeEntity().getName());
    }
}
