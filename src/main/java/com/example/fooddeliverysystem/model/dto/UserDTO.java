package com.example.fooddeliverysystem.model.dto;

import com.example.fooddeliverysystem.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int id;
    private String email;
    private String name;
    private String lastName;
    private String imagePath;
    private int status;
    private String role;

    public static UserDTO mapper(UserEntity userEntity) {
        UserDTO user = new UserDTO();
        user.setEmail(userEntity.getEmail());
        user.setId(userEntity.getId().intValue());
        user.setImagePath(userEntity.getImagePath());
        user.setLastName(userEntity.getLastName());
        user.setName(userEntity.getName());
        user.setRole(userEntity.getRole().getName());
        user.setStatus(userEntity.getStatus().getValue());

        return user;
    }
}
