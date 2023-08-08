package com.example.fooddeliverysystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.entity.UserOrderEntity;
import com.example.fooddeliverysystem.model.dto.MealOrderingDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.model.response.OrderResponse;
import com.example.fooddeliverysystem.service.order.OrderingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderingService orderingService;

    @CrossOrigin
    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<OrderResponse>>> order(@RequestBody @Valid List<MealOrderingDTO> meals,
            @RequestHeader("Authorization") String token) {
        UserEntity user = orderingService.getUser(token);
        if (user == null) {
            return new ResponseEntity<>(BaseResponse.<List<OrderResponse>>builder()
                    .data(null)
                    .status("failed")
                    .message("cannot add order of invalid user")
                    .build(), HttpStatus.NOT_ACCEPTABLE);
        }

        List<UserOrderEntity> ordering = orderingService.ordering(meals, user);
        if (ordering == null) {
            return new ResponseEntity<>(BaseResponse.<List<OrderResponse>>builder()
                    .data(null)
                    .status("failed")
                    .message("cannot add order of requested user")
                    .build(), HttpStatus.NOT_MODIFIED);
        }

        if (ordering.isEmpty()) {
            return new ResponseEntity<>(BaseResponse.<List<OrderResponse>>builder()
                    .data(null)
                    .status("failed")
                    .message("cannot add order of requested user")
                    .build(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(BaseResponse.<List<OrderResponse>>builder()
                .data(OrderResponse.fromEntities(ordering))
                .status("failed")
                .message("cannot add order of requested user")
                .build(), HttpStatus.NOT_MODIFIED);
    }
}
