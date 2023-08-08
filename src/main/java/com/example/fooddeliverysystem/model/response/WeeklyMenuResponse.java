package com.example.fooddeliverysystem.model.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.example.fooddeliverysystem.entity.DailyMenuEntity;
import com.example.fooddeliverysystem.entity.WeeklyMenuEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WeeklyMenuResponse {
    private Long id;

    private Date dateFrom;

    private Date dateTo;

    private String imagePath;

    private List<DailyMenuResponse> dailyMenus;

    public static WeeklyMenuResponse fromWeeklyMenuEntity(WeeklyMenuEntity weeklyMenuEntity) {
        List<DailyMenuResponse> dailyMenuResponses = new ArrayList<>();
        if (dailyMenuResponses != null) {
            for (DailyMenuEntity dailyMenuEntity : weeklyMenuEntity.getDailyMenus()) {
                DailyMenuResponse fromDailyMenuEntity = DailyMenuResponse.fromDailyMenuEntity(dailyMenuEntity);

                dailyMenuResponses.add(fromDailyMenuEntity);
            }
        }
        WeeklyMenuResponse weeklyMenuResponse = builder()
                .dailyMenus(dailyMenuResponses)
                .dateFrom(weeklyMenuEntity.getDateFrom())
                .dateTo(weeklyMenuEntity.getDateTo())
                .id(weeklyMenuEntity.getId())
                .imagePath(weeklyMenuEntity.getImagePath())
                .build();

        return weeklyMenuResponse;
    }

    public static Collection<WeeklyMenuResponse> fromWeeklyMenuEntities(
            Collection<WeeklyMenuEntity> weeklyMenuEntities) {
        Collection<WeeklyMenuResponse> weeklyMenuResponses = new ArrayList<>();
        if (weeklyMenuEntities != null) {
            for (WeeklyMenuEntity weeklyMenuEntity : weeklyMenuEntities) {

                weeklyMenuResponses.add(fromWeeklyMenuEntity(weeklyMenuEntity));
            }
        }

        return weeklyMenuResponses;
    }
}
