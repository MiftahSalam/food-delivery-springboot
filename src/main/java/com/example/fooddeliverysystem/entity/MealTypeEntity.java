package com.example.fooddeliverysystem.entity;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "meals_types")
public class MealTypeEntity implements Serializable {
    @SuppressWarnings(value = "unused")
    private static final Long SerialVersionID = 1L;

    // @EmbeddedId
    // private MealTypePK id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meal_id", referencedColumnName = "id")
    // @MapsId("meal_id")
    private MealEntity meal;

    @ManyToOne
    @JoinColumn(name = "type_entity_id", referencedColumnName = "id")
    // @MapsId("type_entity_id")
    private TypeEntity typeEntity;

    @ManyToMany(mappedBy = "mealTypes")
    private List<UserOrderEntity> userOrders;

    public void addUserOrder(UserOrderEntity userOrderEntity) {
        for (UserOrderEntity userOrder : userOrders) {
            if (userOrderEntity.getId() == userOrder.getId()) {
                return;
            }
        }

        userOrders.add(userOrderEntity);
    }

    public boolean removeUserOrder(UserOrderEntity userOrderEntity) {
        for (UserOrderEntity userOrder : userOrders) {
            if (userOrder.getId() == userOrderEntity.getId()) {
                userOrders.remove(userOrder);

                return true;
            }
        }

        return false;
    }
}
