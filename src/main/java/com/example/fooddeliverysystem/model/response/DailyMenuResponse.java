package com.example.fooddeliverysystem.model.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.example.fooddeliverysystem.entity.DailyMenuEntity;
import com.example.fooddeliverysystem.entity.MealEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class DailyMenuResponse {
    private Long id;

    private Date date;

    private List<MealResponse> meals;

    public static DailyMenuResponse fromDailyMenuEntity(DailyMenuEntity dailyMenuEntity) {
        List<MealResponse> mealResponses = new ArrayList<>();
        if (dailyMenuEntity.getMeals() != null) {
            for (MealEntity mealEntity : dailyMenuEntity.getMeals()) {
                MealResponse mealResponse = MealResponse.fromMealEntity(mealEntity);

                mealResponses.add(mealResponse);
            }
        }

        DailyMenuResponse dailyMenuResponse = builder()
                .date(dailyMenuEntity.getDate())
                .id(dailyMenuEntity.getId())
                .meals(mealResponses)
                .build();

        return dailyMenuResponse;
    }

    public static Collection<DailyMenuResponse> fromDailyMenuEntities(
            Collection<DailyMenuEntity> dailyMenuEntities) {
        Collection<DailyMenuResponse> dailyMenuResponses = new ArrayList<>();
        if (dailyMenuEntities != null) {
            for (DailyMenuEntity dailyMenuEntity : dailyMenuEntities) {

                dailyMenuResponses.add(fromDailyMenuEntity(dailyMenuEntity));
            }
        }

        return dailyMenuResponses;
    }

}
