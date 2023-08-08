package com.example.fooddeliverysystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fooddeliverysystem.entity.RoleEntity;
import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.model.UserStatus;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findFirstByEmailAndStatus(String email, UserStatus status);

    Optional<UserEntity> findFirstByEmail(String email);

    Optional<UserEntity> findFirstByRole(RoleEntity role);

    Optional<UserEntity> findFirstById(Long id);

    Optional<UserEntity> findFirstByIdAndStatus(Long id, UserStatus status);

}
