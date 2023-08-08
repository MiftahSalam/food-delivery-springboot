package com.example.fooddeliverysystem.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.fooddeliverysystem.entity.ConfirmationTokenEntity;
import com.example.fooddeliverysystem.entity.RoleEntity;
import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.model.UserStatus;

@SpringBootTest
public class ConfirmationTokenRepositoryTest {
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        RoleEntity role = roleRepository.findFirstByNameIgnoreCase("USER").orElse(null);
        UserEntity user1 = new UserEntity();
        user1.setEmail("user1@example.com");
        user1.setImagePath("http://asdf/sdfdf/png");
        user1.setLastName("salam");
        user1.setName("user1");
        user1.setPassword("rahasia");
        user1.setRole(role);
        user1.setStatus(UserStatus.ACTIVE);

        userRepository.save(user1);

        ConfirmationTokenEntity token = new ConfirmationTokenEntity();
        token.setUser(user1);
        token.setConfirmedToken("test");

        confirmationTokenRepository.save(token);

    }

    @AfterEach
    void cleanUp() {
        confirmationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindFirstByToken() {
        ConfirmationTokenEntity token = confirmationTokenRepository.findFirstByConfirmedToken("test")
                .orElseThrow(() -> new RuntimeException("user null"));
        assertEquals("test", token.getConfirmedToken());
        assertEquals("http://asdf/sdfdf/png", token.getUser().getImagePath());

    }

    @Test
    void testFindFirstByTokenNotFound() {
        ConfirmationTokenEntity token = confirmationTokenRepository.findFirstByConfirmedToken("salah")
                .orElse(null);
        assertNull(token);
    }
}
