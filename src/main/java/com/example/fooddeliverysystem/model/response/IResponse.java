package com.example.fooddeliverysystem.model.response;

public interface IResponse<From, To> {
    public To fromEntity(From src);
}
