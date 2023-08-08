package com.example.fooddeliverysystem.service.menu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fooddeliverysystem.entity.DailyMenuEntity;
import com.example.fooddeliverysystem.entity.WeeklyMenuEntity;
import com.example.fooddeliverysystem.model.dto.CreateWeeklyMenuDTO;
import com.example.fooddeliverysystem.repository.DailyMenRepository;
import com.example.fooddeliverysystem.repository.WeeklyMenuRepo;

@Service
public class WeeklyMenuServiceImpl implements WeeklyMenuService {
    @Autowired
    private DailyMenRepository dailyMenRepository;

    @Autowired
    private WeeklyMenuRepo weeklyMenuRepo;

    WeeklyMenuServiceImpl(DailyMenRepository dailyMenRepository, WeeklyMenuRepo weeklyMenuRepo) {
        this.dailyMenRepository = dailyMenRepository;
        this.weeklyMenuRepo = weeklyMenuRepo;
    }

    @Override
    public WeeklyMenuEntity getCurrentWeekMenu() {
        Date today = new Date();
        List<WeeklyMenuEntity> weeklyMenuEntities = weeklyMenuRepo.findAll();
        for (WeeklyMenuEntity weeklyMenuEntity : weeklyMenuEntities) {
            if (weeklyMenuEntity.getDateFrom().compareTo(today) * today.compareTo(weeklyMenuEntity.getDateTo()) >= 0) {
                return weeklyMenuEntity;
            }
        }

        return null;
    }

    @Override
    public WeeklyMenuEntity saveWeeklyMenu(CreateWeeklyMenuDTO weeklyMenuDTO) {
        if (weeklyMenuDTO == null) {
            return null;
        }

        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.setTime(weeklyMenuDTO.fromDate());
        calendarFrom.add(Calendar.DATE, 1);

        Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTime(weeklyMenuDTO.toDate());
        calendarTo.add(Calendar.DATE, 1);

        List<WeeklyMenuEntity> oldWeeklyMenus = weeklyMenuRepo
                .findAllByDateFromAfterAndDateToBefore(calendarFrom.getTime(), calendarTo.getTime());
        List<WeeklyMenuEntity> weeklyMenus = weeklyMenuRepo.findAll();

        if (oldWeeklyMenus != null && oldWeeklyMenus.size() > 0) {
            return null;
        }

        for (WeeklyMenuEntity weeklyMenuEntity : weeklyMenus) {
            if (calendarFrom.getTime().compareTo(weeklyMenuEntity.getDateFrom())
                    * weeklyMenuEntity.getDateFrom().compareTo(calendarTo.getTime()) >= 0 ||
                    calendarFrom.getTime().compareTo(weeklyMenuEntity.getDateTo())
                            * weeklyMenuEntity.getDateTo().compareTo(calendarTo.getTime()) >= 0) {
                return null;
            }
        }

        WeeklyMenuEntity weeklyMenu = new WeeklyMenuEntity();
        weeklyMenu.setDateFrom(calendarFrom.getTime());
        weeklyMenu.setDateTo(calendarTo.getTime());
        weeklyMenu.setImagePath(weeklyMenuDTO.imagePath());

        weeklyMenu = weeklyMenuRepo.save(weeklyMenu);

        return createDailyMenus(weeklyMenu);
    }

    @Override
    public Collection<WeeklyMenuEntity> getAllWeeklyMenu() {
        return weeklyMenuRepo.findAll();
    }

    @Override
    public Collection<WeeklyMenuEntity> getWeeklyMenusForDailyMenu() {
        List<WeeklyMenuEntity> weeklyMenuEntities = weeklyMenuRepo.findAll();
        Date todayDate = new Date();
        List<WeeklyMenuEntity> weeklyMenusRet = new ArrayList<WeeklyMenuEntity>();
        for (WeeklyMenuEntity weeklyMenuEntity : weeklyMenuEntities) {
            if (!weeklyMenuEntity.getDateTo().before(todayDate) && !weeklyMenuEntity.finishCreation()) {
                weeklyMenusRet.add(weeklyMenuEntity);
            }
        }

        return weeklyMenusRet;
    }

    private WeeklyMenuEntity createDailyMenus(WeeklyMenuEntity weeklyMenu) {
        Date day = weeklyMenu.getDateFrom();
        Calendar c = Calendar.getInstance();
        c.setTime(weeklyMenu.getDateTo());
        c.add(Calendar.DATE, 1);

        while (day.before(c.getTime())) {
            DailyMenuEntity dailyMenuEntity = new DailyMenuEntity();
            dailyMenuEntity.setDate(day);
            dailyMenuEntity.setWeeklyMenu(weeklyMenu);
            dailyMenRepository.save(dailyMenuEntity);

            weeklyMenu.addDailyMenus(dailyMenuEntity);
            weeklyMenu = weeklyMenuRepo.save(weeklyMenu);

            Calendar c1 = Calendar.getInstance();
            c1.setTime(day);
            c1.add(Calendar.DATE, 1);
            day = c1.getTime();
        }

        return weeklyMenu;
    }
}
