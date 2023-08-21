package com.example.fooddeliverysystem.model.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.fooddeliverysystem.entity.MealTypeEntity;
import com.example.fooddeliverysystem.entity.UserOrderEntity;
import com.example.fooddeliverysystem.model.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderResponse {
    private Long id;

    private Date date;

    private boolean paid;

    private MealTypeResponse mealType;

    private UserDTO user;

    public static OrderResponse fromEntity(UserOrderEntity orderEntity, MealTypeResponse mealTypeResponse) {
        OrderResponse orderResponse = builder()
                .date(orderEntity.getDate())
                .id(orderEntity.getId())
                .mealType(mealTypeResponse)
                .paid(orderEntity.isPaid())
                .user(UserDTO.mapper(orderEntity.getUser()))
                .build();

        return orderResponse;
    }

    public static List<OrderResponse> fromEntities(
            List<UserOrderEntity> orderEntities) {
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (UserOrderEntity orderEntity : orderEntities) {
            if (orderEntity.getMealTypes() != null) {
                for (MealTypeEntity mealTypeEntity : orderEntity.getMealTypes()) {
                    MealTypeResponse mealTypeResponse = MealTypeResponse.fromEntity(mealTypeEntity);
                    orderResponses.add(fromEntity(orderEntity, mealTypeResponse));
                }
            }
        }

        return orderResponses;
    }
}
