package com.example.fooddeliverysystem.repository;

import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionOperations;

import com.example.fooddeliverysystem.entity.ConfirmationTokenEntity;
import com.example.fooddeliverysystem.entity.RoleEntity;
import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.model.UserStatus;

@SpringBootTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private TransactionOperations transactionOperations;

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

        List<ConfirmationTokenEntity> tokenEntities = List.of("test1", "test2", "test3").stream()
                .map(val -> generateTokenEntity(new ConfirmationTokenEntity(), val, user1))
                .collect(Collectors.toList());

        confirmationTokenRepository.saveAll(tokenEntities);

        // user1.setConfirmationTokens(tokenEntities);

    }

    private ConfirmationTokenEntity generateTokenEntity(ConfirmationTokenEntity tokenEntity, String token,
            UserEntity userEntity) {
        tokenEntity.setConfirmedToken(token);
        tokenEntity.setUser(userEntity);
        return tokenEntity;
    }

    @AfterEach
    void cleanUp() {
        confirmationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindFirstByEmail() {
        transactionOperations.executeWithoutResult(stratus -> {
            UserEntity userEntity = userRepository.findFirstByEmail("user1@example.com")
                    .orElseThrow(() -> new RuntimeException("user null"));
            assertEquals("user1", userEntity.getName());
            assertEquals("USER", userEntity.getRole().getName());
            assertEquals(3, userEntity.getConfirmationTokens().size());

        });
    }

    @Test
    void testFindFirstByEmailFailed() {
        transactionOperations.executeWithoutResult(stratus -> {
            UserEntity userEntity = userRepository.findFirstByEmail("salah@example.com")
                    .orElse(null);
            assertNull(userEntity);
        });
    }

    @Test
    void testFindFirstByEmailAndStatus() {
        transactionOperations.executeWithoutResult(stratus -> {
            UserEntity userEntity = userRepository.findFirstByEmailAndStatus("user1@example.com", UserStatus.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("user null"));
            assertEquals("user1", userEntity.getName());
            assertEquals("USER", userEntity.getRole().getName());
            assertEquals(3, userEntity.getConfirmationTokens().size());
        });
    }

    @Test
    void testFindFirstByEmailAndStatusFailed() {
        transactionOperations.executeWithoutResult(stratus -> {
            UserEntity userEntity = userRepository.findFirstByEmailAndStatus("salah@example.com", UserStatus.BANNED)
                    .orElse(null);
            assertNull(userEntity);
        });
    }
}
