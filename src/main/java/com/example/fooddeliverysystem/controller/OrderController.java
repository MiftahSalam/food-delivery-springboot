package com.example.fooddeliverysystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.entity.UserOrderEntity;
import com.example.fooddeliverysystem.model.dto.MealOrderingDTO;
import com.example.fooddeliverysystem.model.dto.MealTypeDTO;
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
                .status("ok")
                .message("success add order of requested user")
                .build(), HttpStatus.OK);
    }

    @CrossOrigin
    @DeleteMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<String>> deleteOrder(@PathVariable("orderId") Long orderId,
            @RequestHeader("Authorization") String token) {
        UserEntity user = orderingService.getUser(token);
        if (user == null) {
            return new ResponseEntity<>(BaseResponse.<String>builder()
                    .data(null)
                    .status("failed")
                    .message("cannot delete order of requested user")
                    .build(), HttpStatus.NOT_MODIFIED);
        }

        try {
            if (orderingService.deleteOrder(orderId.intValue(), user)) {
                return new ResponseEntity<>(BaseResponse.<String>builder()
                        .data("order deleted")
                        .status("ok")
                        .message("success")
                        .build(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(BaseResponse.<String>builder()
                        .data(null)
                        .status("failed")
                        .message("cannot delete order of requested user")
                        .build(), HttpStatus.NOT_MODIFIED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(BaseResponse.<String>builder()
                    .data(null)
                    .status("failed")
                    .message("cannot delete order of requested user")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<MealTypeDTO>>> getAllOrders(@RequestParam("forDay") String forDay,
            @RequestHeader("Authorization") String token) {
        List<MealTypeDTO> gerAllOrders = orderingService.gerAllOrders(forDay);
        if (gerAllOrders.isEmpty()) {
            return new ResponseEntity<>(BaseResponse.<List<MealTypeDTO>>builder()
                    .data(null)
                    .status("empty")
                    .message("empty orders for day")
                    .build(), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(BaseResponse.<List<MealTypeDTO>>builder()
                .data(gerAllOrders)
                .status("ok")
                .message("success get orders for day")
                .build(), HttpStatus.OK);
    }

    @GetMapping("/allOrders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<MealTypeDTO>>> getOrdering(@RequestParam("forDay") String forDay,
            @RequestHeader("Authorization") String token) {
        UserEntity user = orderingService.getUser(token);
        if (user == null) {
            return new ResponseEntity<>(BaseResponse.<List<MealTypeDTO>>builder()
                    .data(null)
                    .status("failed")
                    .message("cannot get order of requested user")
                    .build(), HttpStatus.BAD_REQUEST);
        }

        List<MealTypeDTO> getOrdering = orderingService.getOrdering(forDay, user);
        if (getOrdering.isEmpty()) {
            return new ResponseEntity<>(BaseResponse.<List<MealTypeDTO>>builder()
                    .data(null)
                    .status("empty")
                    .message("empty orders for day")
                    .build(), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(BaseResponse.<List<MealTypeDTO>>builder()
                .data(getOrdering)
                .status("ok")
                .message("success get ordering")
                .build(), HttpStatus.OK);
    }
}
