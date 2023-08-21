package com.example.fooddeliverysystem.service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.entity.MealTypeEntity;
import com.example.fooddeliverysystem.entity.UserOrderEntity;
import com.example.fooddeliverysystem.entity.ViberSenderEntity;
import com.example.fooddeliverysystem.model.dto.MealTypeDTO;
import com.example.fooddeliverysystem.repository.MealRepository;
import com.example.fooddeliverysystem.repository.TypeRepository;
import com.example.fooddeliverysystem.repository.UserOrderRepository;
import com.example.fooddeliverysystem.repository.ViberSenderRepository;

@Service
public class ViberServiceImpl implements ViberService {
    private static final String VIBER_API_URL = "https://chatapi.viber.com/pa/broadcast_message";
    private static final String HEADER_NAME = "X-Viber-Auth-Token";

    @Autowired
    private UserOrderRepository userOrderRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private ViberSenderRepository viberSenderRepository;

    @Value("${viber.X-Viber-Auth-Token-Value}")
    private String viberXViberAuthToken;

    @Value("${viber.message.header.normalOrder}")
    private String viberMessageHeaderNormalOrder;

    @Value("${viber.message.header.earlyOrder}")
    private String viberMessageHeaderEarlyOrder;

    @Value("${viber.message.footer}")
    private String viberMessageFooter;

    @Value("${viber.message.dateFormat}")
    private String viberMessageDateFormat;

    @Override
    public ViberSenderEntity insert(String userId) {
        ViberSenderEntity viberSenderEntity = new ViberSenderEntity();
        viberSenderEntity.setUserId(userId);
        viberSenderEntity.setDate(new Date());

        return viberSenderRepository.save(viberSenderEntity);
    }

    @Scheduled(cron = "${viber.timer.generateEventForNormalOrders}")
    private void generateEventForOrderAt10PM() {
        List<MealTypeDTO> mealTypeDTOs = getOrders(false, new Date());
        Map<MealTypeDTO, Integer> freqMap = mealCounter(mealTypeDTOs);
        String message = generateMessage(freqMap, false);

        if (message == null) {
            return;
        }

        sendMessage(message);
    }

    @Scheduled(cron = "${viber.timer.generateEventForEarlyOrders}")
    private void generateEventForEarlyOrder() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);

        List<MealTypeDTO> mealTypeDTOs = getOrders(true, new Date());
        Map<MealTypeDTO, Integer> freqMap = mealCounter(mealTypeDTOs);
        String message = generateMessage(freqMap, false);

        if (message == null) {
            return;
        }

        sendMessage(message);
    }

    private void sendMessage(String messageToSend) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HEADER_NAME, viberXViberAuthToken);

        List<ViberSenderEntity> result = viberSenderRepository.findDistinct();
        JSONArray broadCastList = new JSONArray();

        for (ViberSenderEntity vEntity : result) {
            broadCastList.add(vEntity.getUserId());
        }

        JSONObject message = new JSONObject();
        JSONObject sender = new JSONObject();

        sender.put("name", "Simple Meal");
        sender.put("avatar", "http://avatar.example.com");

        message.put("sender", sender);
        message.put("min_api_version", "2");
        message.put("type", "text");
        message.put("test", messageToSend);
        message.put("broadcast_list", broadCastList);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<JSONObject> httpEntity = new HttpEntity<JSONObject>(message, headers);

        restTemplate.postForEntity(VIBER_API_URL, httpEntity, JSONObject.class);

    }

    private String generateMessage(Map<MealTypeDTO, Integer> freqMap, boolean earlyOrder) {
        if (freqMap.isEmpty()) {
            return null;
        }

        StringBuilder messaStringBuilder = new StringBuilder();
        Date date = new Date();
        Calendar c = Calendar.getInstance();

        c.setTime(date);

        if (earlyOrder) {
            c.add(Calendar.DATE, 1);
            String day = LocalDate.parse(c.getTime().toString()).getDayOfWeek().toString();

            messaStringBuilder.append("Early order for " + day + "\n\n");
        } else {
            String day = LocalDate.parse(c.getTime().toString()).getDayOfWeek().toString();

            messaStringBuilder.append("Order for " + day + "\n\n");
        }

        for (Map.Entry<MealTypeDTO, Integer> entry : freqMap.entrySet()) {
            messaStringBuilder.append(entry.getKey() + " x " + entry.getValue() + "\n\n");
        }

        messaStringBuilder.append(viberMessageFooter);

        return messaStringBuilder.toString();
    }

    private Map<MealTypeDTO, Integer> mealCounter(List<MealTypeDTO> mealTypeDTOs) {
        Map<MealTypeDTO, Integer> freqMap = new HashMap<>();
        for (MealTypeDTO mealTypeDTO : mealTypeDTOs) {
            if (!freqMap.containsKey(mealTypeDTO)) {
                freqMap.put(mealTypeDTO, 1);
            } else {
                freqMap.put(mealTypeDTO, freqMap.get(mealTypeDTO) + 1);
            }
        }

        return freqMap;
    }

    private List<MealTypeDTO> getOrders(boolean earlyOrder, Date date) {
        List<UserOrderEntity> userOrderEntities = userOrderRepository.findAllByDate(date);
        List<MealTypeDTO> mealTypeDTOs = new LinkedList<>();

        for (UserOrderEntity userOrderEntity : userOrderEntities) {
            for (MealTypeEntity mealTypeEntity : userOrderEntity.getMealTypes()) {
                MealEntity mealEntity = mealRepository.findById((long) mealTypeEntity.getId()).orElse(null);
                // MealEntity mealEntity = mealRepository.findById((long)
                // mealTypeEntity.getId().getMealId()).orElse(null);

                if (earlyOrder) {
                    if (!mealEntity.isEarlyOrder()) {
                        continue;
                    }
                } else {
                    if (mealEntity.isEarlyOrder()) {
                        continue;
                    }
                }

                MealTypeDTO mealTypeDTO = new MealTypeDTO();
                mealTypeDTO.setMealEntity(mealEntity);
                mealTypeDTO.setTypeEntity(typeRepository.findById((long) mealTypeEntity.getId()).get());
                // mealTypeDTO.setTypeEntity(typeRepository.findById((long)
                // mealTypeEntity.getId().getTypeId()).get());
                mealTypeDTO.setUserOrderId(0L);
                mealTypeDTOs.add(mealTypeDTO);
            }
        }

        return mealTypeDTOs;
    }

}
