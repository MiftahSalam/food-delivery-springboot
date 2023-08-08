package com.example.fooddeliverysystem.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fooddeliverysystem.entity.WeeklyMenuEntity;

public interface WeeklyMenuRepo extends JpaRepository<WeeklyMenuEntity, Long> {
    List<WeeklyMenuEntity> findAllByDateFromAfterAndDateToBefore(Date fromDate, Date toDate);

    List<WeeklyMenuEntity> findAllByDateToAfter(Date fromDate);
}
