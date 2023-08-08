package com.example.fooddeliverysystem.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.repository.UserOrderRepository;
import com.example.fooddeliverysystem.repository.UserRepository;
import com.example.fooddeliverysystem.security.TokenUtils;
import com.nimbusds.jose.shaded.gson.JsonObject;

@Service
public class NotificationServiceImpl implements NotificationService {
    private static final String URL = "https://fcm.googleapis.com/fcm/send";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserOrderRepository userOrderRepository;

    @Autowired
    private Environment environment;

    @Override
    public boolean saveToken(String token, UserEntity userEntity) throws IOException {
        if (token.isEmpty()) {
            return false;
        }

        userEntity.setToken(token);
        userRepository.save(userEntity);

        return true;
    }

    @Override
    public UserEntity getUser(String token) {
        if (token.isEmpty()) {
            return null;
        }

        return userRepository.findFirstByEmail(tokenUtils.getUsernameFromToken(token)).orElse(null);
    }

    @Scheduled(cron = "${notification.orderFood.timer}")
    public void orderFoodNotification() {
        List<String> tokens = getNotifiedUsers(true);
        sendNotification(notidicationOrderFoodTitle(), notidicationOrderFoodBody(), tokens);
    }

    @Scheduled(cron = "${notification.orderFoodLast.timer}")
    public void orderFoodLastNotification() {
        List<String> tokens = getNotifiedUsers(true);
        sendNotification(notidicationLastOrderFoodTitle(), notidicationLastOrderFoodBody(), tokens);
    }

    @Scheduled(cron = "${notification.orderFoodLastForTomorrow.timer}")
    public void orderFoodLastNotificationForTomorrow() {
        List<String> tokens = getNotifiedUsers(false);
        sendNotification(notidicationLastOrderFoodForTomorrowTitle(), notidicationLastOrderFoodForTomorrowBody(),
                tokens);
    }

    private void sendNotification(String title, String body, List<String> tokens) {
        try {
            java.net.URL url = new java.net.URL(URL);
            for (String token : tokens) {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorizatn", notificationAuthKey());
                connection.setDoOutput(true);

                JsonObject foodNotif = new JsonObject();
                JsonObject notif = new JsonObject();

                notif.addProperty("title", title);
                notif.addProperty("body", body);
                notif.addProperty("click_action", frontendUrl());

                foodNotif.add("notificatin", notif);
                foodNotif.addProperty("to", token);

                sendRequest(connection, foodNotif.toString());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendRequest(HttpURLConnection connection, String jsonInputString) throws IOException {
        try (OutputStream outputStream = connection.getOutputStream();) {
            byte[] inputs = jsonInputString.getBytes(StandardCharsets.UTF_8);
            outputStream.write(inputs, 0, inputs.length);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));) {
            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = bufferedReader.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private List<String> getNotifiedUsers(boolean today) {
        Date date;
        if (today) {
            date = new Date();
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE, 1);
            date = c.getTime();
        }

        List<UserEntity> userEntities = userRepository.findAll();
        List<String> tokens = new ArrayList<>();

        for (UserEntity userEntity : userEntities) {
            if (userOrderRepository.findByUserIdAndDate(userEntity.getId(), date).isEmpty()) {
                if (userEntity.getToken() != null) {
                    tokens.add(userEntity.getToken());
                }
            }
        }

        return tokens;
    }

    private String frontendUrl() {
        return environment.getProperty("url.frontend");
    }

    private String notidicationOrderFoodTitle() {
        return environment.getProperty("notification.orderFood.title");
    }

    private String notidicationOrderFoodBody() {
        return environment.getProperty("notification.orderFood.body");
    }

    private String notidicationLastOrderFoodTitle() {
        return environment.getProperty("notification.orderFoodLast.title");
    }

    private String notidicationLastOrderFoodBody() {
        return environment.getProperty("notification.orderFoodLast.body");
    }

    private String notidicationLastOrderFoodForTomorrowTitle() {
        return environment.getProperty("notification.orderFoodLastForTomorrow.title");
    }

    private String notidicationLastOrderFoodForTomorrowBody() {
        return environment.getProperty("notification.orderFoodLastForTomorrow.body");
    }

    private String notificationAuthKey() {
        return environment.getProperty("notification.authorization.key");
    }
}
