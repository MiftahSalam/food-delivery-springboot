package com.example.fooddeliverysystem.entity;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "types")
public class TypeEntity implements Serializable {
    @SuppressWarnings(value = "unused")
    private static final Long SerialVersionID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double price;

    private boolean reguler;

    @ManyToMany
    @JoinTable(name = "meals_types", joinColumns = @JoinColumn(name = "type_entity_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "meal_id", referencedColumnName = "id"))
    private List<MealEntity> meals;

}
