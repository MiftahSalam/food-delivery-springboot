package com.example.fooddeliverysystem.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_orders")
public class UserOrderEntity implements Serializable {
    @SuppressWarnings(value = "unused")
    private static final Long SerialVersionID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date date;

    private boolean paid;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "meal_types_user_orders", inverseJoinColumns = {
            @JoinColumn(name = "meal_type_id", referencedColumnName = "id"),
    }, joinColumns = @JoinColumn(name = "user_order_id", referencedColumnName = "id"))
    private List<MealTypeEntity> mealTypes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "id")
    private UserEntity user;
}
