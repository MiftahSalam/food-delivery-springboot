package com.example.fooddeliverysystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fooddeliverysystem.entity.TypeEntity;

public interface TypeRepository extends JpaRepository<TypeEntity, Long> {
    Optional<TypeEntity> findFirstByNameIgnoreCaseAndPrice(String name, double price);

    Optional<TypeEntity> findFirstByNameIgnoreCaseAndPriceAndReguler(String name, double price, boolean reguler);

    List<TypeEntity> findAllByNameIgnoreCase(String name);

    List<TypeEntity> findAllByRegulerTrue();
}
