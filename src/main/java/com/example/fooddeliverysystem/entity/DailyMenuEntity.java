package com.example.fooddeliverysystem.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
// import lombok.Getter;
import lombok.NoArgsConstructor;
// import lombok.Setter;

@ApiModel
// @Getter
// @Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "daily_menus")
public class DailyMenuEntity implements Serializable {
    @SuppressWarnings(value = "unused")
    private static final Long SerialVersionID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToMany(mappedBy = "dailyMenus")
    private List<MealEntity> meals;

    @ManyToOne
    @JoinColumn(name = "weekly_menu_id", referencedColumnName = "id")
    private WeeklyMenuEntity weeklyMenu;

}
