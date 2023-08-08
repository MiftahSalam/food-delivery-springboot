package com.example.fooddeliverysystem.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "weekly_menus")
public class WeeklyMenuEntity implements Serializable {
    @SuppressWarnings(value = "unused")
    private static final Long SerialVersionID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dateFrom;

    @Temporal(TemporalType.DATE)
    private Date dateTo;

    @Column(name = "image_path")
    private String imagePath;

    @OneToMany(mappedBy = "weeklyMenu")
    private List<DailyMenuEntity> dailyMenus = new ArrayList<>();

    public boolean addDailyMenus(DailyMenuEntity newDailyMenuEntity) {
        for (DailyMenuEntity dailyMenuEntity : dailyMenus) {
            if (dailyMenuEntity.getId() == newDailyMenuEntity.getId()) {
                return false;
            }
        }

        dailyMenus.add(newDailyMenuEntity);
        return true;
    }

    public boolean finishCreation() {
        ArrayList<Date> dates = new ArrayList<Date>();
        Date day = dateFrom;
        Calendar c = Calendar.getInstance();
        c.setTime(dateTo);
        c.add(Calendar.DATE, 1);

        while (day.before(c.getTime())) {
            dates.add(day);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(day);
            c1.add(Calendar.DATE, 1);
            day = c1.getTime();
        }

        if (dates.size() == dailyMenus.size()) {
            return true;
        }

        return false;
    }

    public boolean haveDailyMenu(Date date) {
        for (DailyMenuEntity dailyMenuEntity : dailyMenus) {
            if (dailyMenuEntity.getDate().equals(date)) {
                return true;
            }
        }

        return false;
    }
}
