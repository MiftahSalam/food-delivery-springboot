package com.example.fooddeliverysystem.service.user;

import com.example.fooddeliverysystem.model.dto.MealTypeDTO;
import com.example.fooddeliverysystem.model.dto.UserDTO;

public interface ChosenService {
    void selectChosen();

    void setPaid(MealTypeDTO mealTypeDTO);

    UserDTO getChosen();

    void payingNotification(long[] userIds);
}
