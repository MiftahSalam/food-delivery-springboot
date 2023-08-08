package com.example.fooddeliverysystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.fooddeliverysystem.entity.ViberSenderEntity;

public interface ViberSenderRepository extends JpaRepository<ViberSenderEntity, Long> {
    @Query("select distinct v from ViberSenderEntity v group by v.userId")
    List<ViberSenderEntity> findDistinct();
}
