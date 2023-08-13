package com.example.fooddeliverysystem.service.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;

@SpringBootTest
public class EmailSenderServiceTest {
    @Autowired
    private EmailSenderService emailSenderService;

    @Test
    void testSendEmail() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo("salam.miftah@gmail.com");
        mailMessage.setSubject("Complete Registration");

        StringBuilder messageBodyBuilder = new StringBuilder();
        messageBodyBuilder.append("To confirm your account, please click here :");
        messageBodyBuilder.append("http://localhost:8080/auth/login" + "?token=test-token");
        messageBodyBuilder.append("Simple LLS");

        mailMessage.setText(messageBodyBuilder.toString());

        emailSenderService.sendEmail(mailMessage);

    }
}
