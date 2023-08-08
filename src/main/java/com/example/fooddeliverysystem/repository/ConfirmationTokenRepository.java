package com.example.fooddeliverysystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fooddeliverysystem.entity.ConfirmationTokenEntity;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationTokenEntity, Long> {
    Optional<ConfirmationTokenEntity> findFirstByConfirmedToken(String token);
}
