package com.example.fooddeliverysystem.service.order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.entity.MealTypeEntity;
import com.example.fooddeliverysystem.entity.MealTypePK;
import com.example.fooddeliverysystem.entity.TypeEntity;
import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.entity.UserOrderEntity;
import com.example.fooddeliverysystem.model.dto.MealOrderingDTO;
import com.example.fooddeliverysystem.model.dto.MealTypeDTO;
import com.example.fooddeliverysystem.model.dto.UserDTO;
import com.example.fooddeliverysystem.model.response.MealTypeResponse;
import com.example.fooddeliverysystem.model.response.OrderResponse;
import com.example.fooddeliverysystem.repository.MealRepository;
import com.example.fooddeliverysystem.repository.MealTypeRepository;
import com.example.fooddeliverysystem.repository.TypeRepository;
import com.example.fooddeliverysystem.repository.UserOrderRepository;
import com.example.fooddeliverysystem.repository.UserRepository;
import com.example.fooddeliverysystem.security.TokenUtils;

@Service
public class OrderServiceImpl implements OrderingService {
    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserOrderRepository userOrderRepository;

    @Autowired
    private MealTypeRepository mealTypeRepository;

    @Autowired
    private TokenUtils tokenUtils;

    @Value("${ordering.order.until.hour}")
    private int orderUntilHour;

    @Value("${ordering.order.until.min}")
    private int orderUntilMin;

    @Value("${ordering.early.order.until.hour}")
    private int earlyOrderUntilHour;

    @Value("${ordering.early.order.until.min}")
    private int earlyOrderUntilMin;

    @Override
    public UserEntity getUser(String token) {
        if (token == null) {
            return null;
        }

        String usernameFromToken = tokenUtils.getUsernameFromToken(token);

        return userRepository.findFirstByEmail(usernameFromToken).orElse(null);
    }

    @Override
    public List<UserOrderEntity> ordering(List<MealOrderingDTO> mealOrderingDTOs, UserEntity userEntity) {
        Date todayDate = new Date(System.currentTimeMillis() - 100_000_000); // mock
        // Date todayDate = new Date();
        if (!checkDate(todayDate)) {
            return null;
        }

        ArrayList<UserOrderEntity> userOrderEntities = new ArrayList<UserOrderEntity>();
        Date today10 = getDate(new Date(), orderUntilHour, orderUntilMin, 0);
        Date dateForTomorrow = getDateForTomorrow(todayDate);

        for (MealOrderingDTO mealOrderingDTO : mealOrderingDTOs) {
            MealEntity mealEntity = mealRepository.findById(mealOrderingDTO.mealDTO().getId()).orElse(null);
            TypeEntity typeEntity;

            if (mealEntity.getTypes().get(0).isReguler()) {
                typeEntity = mealEntity.getType(mealOrderingDTO.regular());
            } else {
                typeEntity = mealEntity.getType();
            }

            if (typeEntity == null) {
                return userOrderEntities;
            }

            if (mealEntity.isEarlyOrder()) {
                MealTypeEntity mealTypeTomorrow = getMealType(mealEntity, typeEntity);
                if (mealTypeTomorrow == null) {
                    return userOrderEntities;
                }

                UserOrderEntity userOrderTommorrow;
                for (int index = 0; index < mealOrderingDTO.count(); index++) {
                    userOrderTommorrow = getUserOrder(userRepository.findById(userEntity.getId()).get(),
                            dateForTomorrow);
                    userOrderTommorrow.getMealTypes().add(mealTypeTomorrow);
                    mealTypeTomorrow.addUserOrder(userOrderTommorrow);
                    userOrderEntities.add(userOrderRepository.save(userOrderTommorrow));
                }

                mealTypeRepository.save(mealTypeTomorrow);
            } else {
                if (todayDate.after(today10)) {
                    return null;
                }

                MealTypeEntity mealType = getMealType(mealEntity, typeEntity);
                if (mealType == null) {
                    return new ArrayList<>();
                }

                UserOrderEntity userOrderToday;
                for (int index = 0; index < mealOrderingDTO.count(); index++) {
                    userOrderToday = getUserOrder(userEntity, todayDate);
                    userOrderToday.getMealTypes().add(mealType);
                    mealType.addUserOrder(userOrderRepository.save(userOrderToday));
                }

                mealTypeRepository.save(mealType);
            }
        }

        return userOrderEntities;
    }

    @Override
    public List<OrderResponse> getOrdering(String forDay, UserEntity userEntity) {
        Date date;
        if (forDay.equals("today")) {
            date = new Date();
        } else {
            date = getDateForTomorrow(new Date());
        }

        List<OrderResponse> orderResponses = new ArrayList<>();
        List<UserOrderEntity> userOrderEntities = userOrderRepository.findByUserIdAndDate(userEntity.getId(), date);

        for (UserOrderEntity userOrderEntity : userOrderEntities) {
            for (MealTypeEntity mealTypeEntity : userOrderEntity.getMealTypes()) {
                OrderResponse orderResponse = new OrderResponse();
                MealTypeResponse mealTypeResponse = MealTypeResponse.fromEntity(mealTypeEntity);

                orderResponse.setMealType(mealTypeResponse);
                orderResponse.setId(userOrderEntity.getId());
                orderResponse.setPaid(userOrderEntity.isPaid());

                // mealTypeDTO.setMealEntity(mealRepository.findById(mealTypeEntity.getMeal().getId()).get());
                // mealTypeDTO.setTypeEntity(typeRepository.findById(mealTypeEntity.getTypeEntity().getId()).get());
                // mealTypeDTO.setUserOrderId(userOrderEntity.getId());
                // mealTypeDTO.setPaid(userOrderEntity.isPaid());

                UserDTO userDTO = new UserDTO();
                userDTO.setEmail(userOrderEntity.getUser().getEmail());
                userDTO.setName(userOrderEntity.getUser().getName());
                userDTO.setLastName(userOrderEntity.getUser().getLastName());
                userDTO.setImagePath(userOrderEntity.getUser().getImagePath());
                userDTO.setRole(userOrderEntity.getUser().getRole().getName());
                userDTO.setId(userOrderEntity.getUser().getId().intValue());
                userDTO.setName(userOrderEntity.getUser().getName());

                orderResponse.setUser(userDTO);
                // mealTypeDTO.setUser(userDTO);

                orderResponses.add(orderResponse);
                // mealTypeDTOs.add(mealTypeDTO);
            }
        }

        return orderResponses;
        // return mealTypeDTOs;
    }

    @Override
    public boolean deleteOrder(int orderId, UserEntity userEntity) {
        UserOrderEntity userOrderEntity = userOrderRepository.findById((long) orderId).orElse(null);
        try {
            if (!checkDateForDeleting(userOrderEntity.getDate())) {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        if (userOrderEntity == null) {
            return false;
        }

        for (MealTypeEntity mealTypeEntity : userOrderEntity.getMealTypes()) {
            mealTypeEntity.removeUserOrder(userOrderEntity);
            mealTypeRepository.save(mealTypeEntity);
        }
        userEntity.removeUserOrder(userOrderEntity);
        userRepository.save(userEntity);

        userOrderRepository.deleteById((long) orderId);

        return true;
    }

    @Override
    public List<OrderResponse> gerAllOrders(String forDay) {
        List<OrderResponse> allOrders = new ArrayList<>();

        for (UserEntity userEntity : userRepository.findAll()) {
            allOrders.addAll(getOrdering(forDay, userEntity));
        }

        return allOrders;
    }

    private boolean checkDateForDeleting(Date dateForCheck) {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        Date tomorrow;

        c.setTime(new Date());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.DATE, 1);

        tomorrow = c.getTime();
        if (today.after(getDate(new Date(), orderUntilHour, orderUntilMin, 0)) && dateForCheck.before(tomorrow)) {
            return false;
        } else {
            return today.after(getDate(new Date(), earlyOrderUntilHour, earlyOrderUntilMin, 0));
        }
    }

    private UserOrderEntity getUserOrder(UserEntity userEntity, Date date) {
        UserOrderEntity userOrderEntity = new UserOrderEntity();

        userOrderEntity.setDate(date);
        userOrderEntity.setPaid(false);
        userOrderEntity.setUser(userEntity);
        userEntity.addUserOrder(userOrderEntity);

        userRepository.save(userEntity);

        return userOrderRepository.save(userOrderEntity);
    }

    private MealTypeEntity getMealType(MealEntity mealEntity, TypeEntity typeEntity) {
        // MealTypePK mealTypePK = new MealTypePK();
        // mealTypePK.setMealId(mealEntity.getId().intValue());
        // mealTypePK.setTypeId(typeEntity.getId().intValue());

        // return mealTypeRepository.findById(mealTypePK).orElse(null);
        return mealTypeRepository.findByMealIdAndTypeEntityId(mealEntity.getId(), typeEntity.getId());
    }

    private Date getDateForTomorrow(Date today) {
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE, 1);

        return c.getTime();
    }

    private boolean checkDate(Date dateForCheck) {
        if (dateForCheck.after(getDate(new Date(), orderUntilHour, orderUntilMin, 0))) {
            return !dateForCheck.after(getDate(new Date(), earlyOrderUntilHour, earlyOrderUntilMin, 0));
        }

        return true;
    }

    private Date getDate(Date date, int hour, int minute, int sec) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, sec);

        return calendar.getTime();
    }
}
