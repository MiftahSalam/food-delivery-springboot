package com.example.fooddeliverysystem.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fooddeliverysystem.entity.UserOrderEntity;

public interface UserOrderRepository extends JpaRepository<UserOrderEntity, Long> {
    List<UserOrderEntity> findByUserIdAndDate(Long id, Date date);

    List<UserOrderEntity> findAllByDate(Date date);
}
