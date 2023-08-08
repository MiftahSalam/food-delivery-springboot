package com.example.fooddeliverysystem.repository;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionOperations;

import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.entity.TypeEntity;

@SpringBootTest
public class TypeRepositoryTest {
    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private TransactionOperations transactionOperations;

    final double incrPrice = 12.43;

    @BeforeEach
    void setUp() {
        List<MealEntity> mealEntities = List.of(1, 2, 3).stream().map(val -> generateMealEntity(new MealEntity(), val))
                .collect(Collectors.toList());

        mealRepository.saveAll(mealEntities);

        for (int i = 0; i < 10; i++) {
            TypeEntity typeEntity = new TypeEntity();
            typeEntity.setName("type test");
            typeEntity.setPrice(10.32 + incrPrice);
            typeEntity.setReguler(i < 5);
            typeEntity.setMeals(mealEntities);

            typeRepository.save(typeEntity);
        }
    }

    private MealEntity generateMealEntity(MealEntity mealEntity, Integer val) {
        mealEntity.setName("Meal " + val);
        return mealEntity;
    }

    @AfterEach
    void cleanUp() {
        typeRepository.deleteAll();
        mealRepository.deleteAll();
    }

    @Test
    void testFindFirstByNameIgnoreCaseAndPrice() {
        transactionOperations.executeWithoutResult(status -> {
            TypeEntity typeEntity = typeRepository.findFirstByNameIgnoreCaseAndPrice("type test", 10.32 + incrPrice)
                    .orElseThrow(() -> new RuntimeException("type not found"));
            assertEquals("type test", typeEntity.getName());
            assertEquals(10.32 + incrPrice, typeEntity.getPrice(), 0.01);
            assertEquals(3, typeEntity.getMeals().size());
        });
    }

    @Test
    void testFindAllByRegulerTrue() {
        List<TypeEntity> typeEntities = typeRepository.findAllByRegulerTrue();

        assertEquals(5, typeEntities.size());
    }

    @Test
    void testFindAllByNameIgnoreCase() {
        List<TypeEntity> typeEntities = typeRepository.findAllByNameIgnoreCase("type test");

        assertEquals(10, typeEntities.size());
    }
}
