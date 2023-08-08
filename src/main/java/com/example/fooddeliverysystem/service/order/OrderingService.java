package com.example.fooddeliverysystem.service.order;

import java.util.List;

import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.entity.UserOrderEntity;
import com.example.fooddeliverysystem.model.dto.MealOrderingDTO;
import com.example.fooddeliverysystem.model.dto.MealTypeDTO;

public interface OrderingService {
    UserEntity getUser(String token);

    List<UserOrderEntity> ordering(List<MealOrderingDTO> mealOrderingDTOs, UserEntity userEntity);

    List<MealTypeDTO> getOrdering(String forDay, UserEntity userEntity);

    boolean deleteOrder(int orderId, UserEntity userEntity);

    List<MealTypeDTO> gerAllOrders(String forDay);
}
