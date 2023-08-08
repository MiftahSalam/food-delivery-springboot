package com.example.fooddeliverysystem.entity;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import jakarta.persistence.Column;
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
@Table(name = "meals")
public class MealEntity implements Serializable {
    @SuppressWarnings(value = "unused")
    private static final Long SerialVersionID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(name = "early_order")
    private boolean earlyOrder;

    @Column(name = "is_reguler")
    private boolean isReguler;

    @ManyToMany(mappedBy = "meals")
    private List<TypeEntity> types;

    @ManyToMany
    @JoinTable(name = "daily_menus_meals", joinColumns = @JoinColumn(name = "meal_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "daily_menu_id", referencedColumnName = "id"))
    private List<DailyMenuEntity> dailyMenus;

    public void addDailyMenu(DailyMenuEntity dailyMenuEntity) {
        for (DailyMenuEntity currentDailyMenuEntity : dailyMenus) {
            if (currentDailyMenuEntity.getId() == dailyMenuEntity.getId()) {
                return;
            }
        }

        dailyMenus.add(dailyMenuEntity);
    }

    public void removeDailyMenu(DailyMenuEntity dailyMenuEntity) {
        if (dailyMenus == null && dailyMenus.isEmpty()) {
            return;
        }

        for (DailyMenuEntity currentDailyMenuEntity : dailyMenus) {
            if (currentDailyMenuEntity.getId() == dailyMenuEntity.getId()) {
                dailyMenus.remove(currentDailyMenuEntity);
                return;
            }
        }
    }

    public TypeEntity getType(String name) {
        for (TypeEntity typeEntity : types) {
            if (typeEntity.getName().equals(name)) {
                return typeEntity;
            }
        }

        return null;
    }

    public TypeEntity getType() {
        if (types.size() > 1) {
            return null;
        } else {
            return types.get(0);
        }
    }
}
