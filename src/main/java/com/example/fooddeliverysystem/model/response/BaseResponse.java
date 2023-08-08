package com.example.fooddeliverysystem.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BaseResponse<T> {
    private String status;
    private String message;
    private T data;
}
