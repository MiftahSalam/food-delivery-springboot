package com.example.fooddeliverysystem.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fooddeliverysystem.entity.DailyMenuEntity;

public interface DailyMenRepository extends JpaRepository<DailyMenuEntity, Long> {
    Optional<DailyMenuEntity> findFirstByDate(Date date);
}
