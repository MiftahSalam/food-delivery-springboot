package com.example.fooddeliverysystem.service.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.fooddeliverysystem.entity.RoleEntity;
import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.entity.UserOrderEntity;
import com.example.fooddeliverysystem.model.dto.MealTypeDTO;
import com.example.fooddeliverysystem.model.dto.UserDTO;
import com.example.fooddeliverysystem.repository.RoleRepository;
import com.example.fooddeliverysystem.repository.UserOrderRepository;
import com.example.fooddeliverysystem.repository.UserRepository;
import com.nimbusds.jose.shaded.gson.JsonObject;

@Service
public class ChosenServiceImpl implements ChosenService {
    private static final String URL = "https://fcm.googleapis.com/fcm/send";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserOrderRepository userOrderRepository;

    @Value("${notification.authorization.key}")
    private String notifAuthKey;

    @Value("${chosen.message.notify.title}")
    private String chosenMessageNotifyTitle;

    @Value("${chosen.message.notify.body}")
    private String chosenMessageNotifyBody;

    @Value("${chosen.message.notify.clickAction}")
    private String chosenMessageNotifyClickAction;

    @Value("${chosen.message.eliminate.title}")
    private String chosenMessageEliminateTitle;

    @Value("${chosen.message.eliminate.body}")
    private String chosenMessageEliminateBody;

    @Value("${url.frontend}")
    private String chosenMessageEliminatClickAction;

    @Value("${chosen.message.payNotif.title}")
    private String chosenMessagePayNotifTitle;

    @Value("${chosen.message.payNotif.body}")
    private String chosenMessagePayNotifBody;

    @Value("${url.frontend}")
    private String chosenMessagePayNotifClickAction;

    @Value("${url.frontend}")
    private String urlFrontend;

    @Override
    public void selectChosen() {
        List<UserEntity> userEntities = userRepository.findAll();
        Random random = new Random();
        UserEntity randomUserEntity = userEntities.get(random.nextInt(userEntities.size()));

        while (randomUserEntity.getRole().getName().equals("chosen")
                || userOrderRepository.findByUserIdAndDate(randomUserEntity.getId(), new Date()).isEmpty()) {
            if (!haveUserWithTodayOrders(userEntities)) {
                return;
            }

            userEntities = userRepository.findAll();
            randomUserEntity = userEntities.get(random.nextInt(userEntities.size()));
        }

        RoleEntity choosenRole = roleRepository.findFirstByNameIgnoreCase("CHOOSEN").orElse(null);
        if (choosenRole == null) {
            return;
        }
        choosenRole.getUsers().add(randomUserEntity);
        randomUserEntity.setRole(choosenRole);

        roleRepository.save(choosenRole);
        userRepository.save(randomUserEntity);
    }

    @Override
    public void setPaid(MealTypeDTO mealTypeDTO) {
        UserOrderEntity userOrderEntity = userOrderRepository.findById(mealTypeDTO.getUserOrderId()).get();
        userOrderEntity.setPaid(mealTypeDTO.isPaid());
        userOrderRepository.save(userOrderEntity);
    }

    @Override
    public UserDTO getChosen() {
        UserEntity choosen = userRepository.findFirstByRole(roleRepository.findFirstByNameIgnoreCase("CHOOSEN").get())
                .orElse(null);
        if (choosen != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(choosen.getEmail());
            userDTO.setImagePath(choosen.getImagePath());
            userDTO.setLastName(choosen.getLastName());
            userDTO.setName(choosen.getName());
            userDTO.setRole(choosen.getRole().getName());

            return userDTO;
        }

        return null;
    }

    @Override
    public void payingNotification(long[] userIds) {
        if (userIds.length == 0) {
            throw new IllegalArgumentException("empty usersIds");
        }

        RoleEntity role = roleRepository.findFirstByNameIgnoreCase("CHOOSEN").orElse(null);
        if (role == null) {
            throw new IllegalStateException("chosen role not found");
        }

        UserEntity userchosen = userRepository.findFirstByRole(role).orElse(null);
        if (userchosen == null) {
            throw new IllegalStateException("chosen user not found");
        }

        for (long userid : userIds) {
            UserEntity userEntity = userRepository.findById(userid).orElse(null);
            if (userEntity == null) {
                continue;
            }

            sendNotification(
                    userEntity.getToken(),
                    chosenMessagePayNotifTitle,
                    chosenMessagePayNotifBody + userchosen.getName() + " " + userchosen.getLastName(),
                    chosenMessagePayNotifClickAction);
        }
    }

    @Scheduled(cron = "${chosen.timer.notify}")
    public void notifychosen() {
        selectChosen();
        UserEntity userChoosen = userRepository
                .findFirstByRole(roleRepository.findFirstByNameIgnoreCase("CHOOSEN").orElse(null)).orElse(null);
        if (userChoosen == null) {
            return;
        }

        sendNotification(userChoosen.getToken(), chosenMessageNotifyTitle, chosenMessageNotifyBody,
                chosenMessageNotifyClickAction);
    }

    @Scheduled(cron = "${chosen.timer.eliminate}")
    public void eliminatechosen() {
        UserEntity userChoosen = userRepository
                .findFirstByRole(roleRepository.findFirstByNameIgnoreCase("CHOOSEN").orElse(null)).orElse(null);
        if (userChoosen != null) {
            RoleEntity role = roleRepository.findFirstByNameIgnoreCase("USER").orElse(null);
            userChoosen.setRole(role);
            role.getUsers().add(userChoosen);

            userRepository.save(userChoosen);
            roleRepository.save(role);

            sendNotification(userChoosen.getToken(), chosenMessageEliminateTitle, chosenMessageEliminateBody,
                    chosenMessageEliminatClickAction);
        }
    }

    public String getchosenMessageNotifyClickAction() {
        return urlFrontend + chosenMessageNotifyClickAction;
    }

    public String getchosenMessageEliminateClickAction() {
        return urlFrontend + chosenMessageEliminatClickAction;
    }

    public String getchosenMessagePayNotifClickAction() {
        return urlFrontend + chosenMessagePayNotifClickAction;
    }

    private boolean haveUserWithTodayOrders(List<UserEntity> userEntities) {
        for (UserEntity userEntity : userEntities) {
            if (userEntity.getRole().getName().equals("USER")
                    && !userOrderRepository.findByUserIdAndDate(userEntity.getId(), new Date()).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private void sendNotification(String token, String title, String body, String clickAction) {
        try {
            java.net.URL url = new java.net.URL(URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", this.notifAuthKey);
            connection.setDoOutput(true);

            JsonObject foodNotif = new JsonObject();
            JsonObject notif = new JsonObject();

            notif.addProperty("title", title);
            notif.addProperty("body", body);
            notif.addProperty("click_action", clickAction);

            foodNotif.add("notification", notif);
            foodNotif.addProperty("tok", token);

            sendRequest(connection, clickAction);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendRequest(HttpURLConnection connection, String jsonInputString) {
        try (OutputStream outputStream = connection.getOutputStream();) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));) {
            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }

            System.out.println(responseBuilder.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
