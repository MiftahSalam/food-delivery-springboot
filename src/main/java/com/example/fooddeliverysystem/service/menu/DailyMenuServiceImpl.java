package com.example.fooddeliverysystem.service.menu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.fooddeliverysystem.entity.DailyMenuEntity;
import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.entity.WeeklyMenuEntity;
import com.example.fooddeliverysystem.model.dto.DailyMenuDTO;
import com.example.fooddeliverysystem.model.dto.MealDTO;
import com.example.fooddeliverysystem.model.dto.UpdateDailyMenuDTO;
import com.example.fooddeliverysystem.repository.DailyMenRepository;
import com.example.fooddeliverysystem.repository.MealRepository;
import com.example.fooddeliverysystem.repository.WeeklyMenuRepo;

@Service
public class DailyMenuServiceImpl implements DailyMenuService {
    @Autowired
    private WeeklyMenuRepo weeklyMenuRepo;

    @Autowired
    private DailyMenRepository dailyMenRepository;

    @Autowired
    private MealRepository mealRepository;

    @Value("${ordering.order.until.hour}")
    private int orderUntilHour;

    @Value("${ordering.order.until.min}")
    private int orderUntilMin;

    @Value("${ordering.early.order.until.hour}")
    private int earlyOrderUntilHour;

    @Value("${ordering.early.order.until.min}")
    private int earlyOrderUntilMin;

    DailyMenuServiceImpl(DailyMenRepository dailyMenRepository, WeeklyMenuRepo weeklyMenuRepo,
            MealRepository mealRepository) {
        this.dailyMenRepository = dailyMenRepository;
        this.weeklyMenuRepo = weeklyMenuRepo;
        this.mealRepository = mealRepository;
    }

    @Override
    public DailyMenuEntity getDailyMenuForToday() {
        Date today = new Date();
        Date today10Clock = getDate(today, orderUntilHour, orderUntilMin, 0);
        Date today17Clock = getDate(today, earlyOrderUntilHour, earlyOrderUntilMin, 0);
        DailyMenuEntity menuToday = dailyMenRepository.findFirstByDate(today).orElse(null);
        DailyMenuEntity menuTommorow = dailyMenRepository.findFirstByDate(getTomorrow(today)).orElse(null);
        List<MealEntity> meals = new ArrayList<>();

        if (menuToday != null) {
            for (int i = 0; i < menuToday.getMeals().size(); i++) {
                if (!menuToday.getMeals().get(i).isEarlyOrder() && !today.after(today10Clock)) {
                    meals.add(menuToday.getMeals().get(i));
                }
            }
        }

        if (menuTommorow != null) {
            for (int i = 0; i < menuTommorow.getMeals().size(); i++) {
                if (!menuTommorow.getMeals().get(i).isEarlyOrder() && !today.after(today17Clock)) {
                    meals.add(menuTommorow.getMeals().get(i));
                }
            }
        }

        if (menuToday != null) {
            menuToday.setMeals(meals);

            return menuToday;
        }

        return null;
    }

    @Override
    public Collection<DailyMenuEntity> getDailyMenus(Long weeklyMenuId) {
        WeeklyMenuEntity weeklyMenu = weeklyMenuRepo.findById(weeklyMenuId).orElse(null);
        if (weeklyMenu == null) {
            return new ArrayList<>();
        }

        return weeklyMenu.getDailyMenus();
    }

    @Override
    public DailyMenuEntity saveDailyMenu(DailyMenuDTO menu) {
        DailyMenuEntity dailyMenuEntity = dailyMenRepository.findFirstByDate(menu.date()).orElse(null);
        if (dailyMenuEntity != null) {
            return null;
        }

        WeeklyMenuEntity weeklyMenuEntity = weeklyMenuRepo.findById(menu.weeklyMenu().id()).orElse(null);
        if (weeklyMenuEntity == null) {
            return null;
        }

        if (weeklyMenuEntity.haveDailyMenu(menu.date())) {
            return null;
        }

        if (menu.date().before(weeklyMenuEntity.getDateFrom())
                || menu.date().after(getTomorrow(weeklyMenuEntity.getDateTo()))) {
            return null;
        }

        DailyMenuEntity newDailyMenuEntity = new DailyMenuEntity();
        List<MealEntity> mealEntities = new ArrayList<>();
        for (MealDTO meal : menu.meals()) {
            if (mealRepository.findById(meal.getId()) == null) {
                return null;
            }

            mealEntities.add(meal.toEntity());
        }

        newDailyMenuEntity.setDate(menu.date());
        newDailyMenuEntity.setMeals(mealEntities);
        newDailyMenuEntity.setWeeklyMenu(weeklyMenuEntity);

        DailyMenuEntity savedDailyMenuEntity = dailyMenRepository.save(newDailyMenuEntity);

        weeklyMenuEntity.addDailyMenus(savedDailyMenuEntity);
        weeklyMenuRepo.save(weeklyMenuEntity);

        for (MealDTO meal : menu.meals()) {
            Optional<MealEntity> meal1Optional = mealRepository.findById(meal.getId());
            meal1Optional.get().addDailyMenu(savedDailyMenuEntity);
            mealRepository.save(meal1Optional.get());
        }

        return savedDailyMenuEntity;
    }

    @Override
    public DailyMenuEntity updateDailyMenu(UpdateDailyMenuDTO menu) {
        DailyMenuEntity dailyMenuEntity = dailyMenRepository.findById(menu.dailyMenuID()).orElse(null);
        if (dailyMenuEntity == null) {
            return null;
        }

        if (!checkForUpdate(dailyMenuEntity.getDate())) {
            return null;
        }

        List<MealEntity> mealEntities = new ArrayList<>();
        for (MealDTO mealEntity : menu.meals()) {
            MealEntity mealEntity2 = mealRepository.findById(mealEntity.getId()).orElse(null);
            mealEntity2.removeDailyMenu(dailyMenuEntity);
            mealRepository.save(mealEntity2);

            mealEntities.add(mealEntity2);

        }

        dailyMenuEntity.setMeals(mealEntities);
        DailyMenuEntity savedDailyMenuEntity = dailyMenRepository.save(dailyMenuEntity);

        for (MealDTO mealEntity : menu.meals()) {
            MealEntity mealEntity2 = mealRepository.findById(mealEntity.getId()).orElse(null);
            mealEntity2.addDailyMenu(savedDailyMenuEntity);
            mealRepository.save(mealEntity2);
        }

        return savedDailyMenuEntity;
    }

    @Override
    public Collection<Date> getDays(Long weeklyMenuId) {
        ArrayList<Date> dates = new ArrayList<Date>();
        WeeklyMenuEntity weeklyMenuEntity = weeklyMenuRepo.findById(weeklyMenuId).orElse(null);
        if (weeklyMenuEntity == null) {
            return dates;
        }

        Date day = weeklyMenuEntity.getDateFrom();

        while (day.before(getTomorrow(weeklyMenuEntity.getDateTo()))) {
            dates.add(day);
            day = getTomorrow(day);
        }

        ArrayList<Date> datesRet = new ArrayList<Date>();
        for (Date date : dates) {
            if (!weeklyMenuEntity.haveDailyMenu(date)) {
                datesRet.add(date);
            }
        }

        return datesRet;
    }

    public int getOrderUntilHour() {
        return orderUntilHour;
    }

    public int getOrderUntilMin() {
        return orderUntilMin;
    }

    public int getEarlyOrderUntilHour() {
        return earlyOrderUntilHour;
    }

    public int getEarlyOrderUntilMin() {
        return earlyOrderUntilMin;
    }

    private boolean checkForUpdate(Date dateForCheck) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();

        if (dateForCheck.before(tomorrow)) {
            return false;
        }

        return true;
    }

    private Date getDate(Date date, int hour, int min, int sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);

        return calendar.getTime();
    }

    private Date getTomorrow(Date today) {
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE, 1);

        return c.getTime();
    }
}
