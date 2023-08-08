package com.example.fooddeliverysystem.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionOperations;

import com.example.fooddeliverysystem.entity.RoleEntity;
import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.entity.UserOrderEntity;
import com.example.fooddeliverysystem.model.UserStatus;

@SpringBootTest
public class UserOrderRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserOrderRepository userOrderRepository;

    @Autowired
    private TransactionOperations transactionOperations;

    private final Date now = Date.from(Instant.now());

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

        UserOrderEntity userOrderEntity = new UserOrderEntity();
        userOrderEntity.setUser(user1);
        userOrderEntity.setDate(now);

        UserOrderEntity userOrderEntity1 = new UserOrderEntity();
        userOrderEntity1.setUser(user1);
        userOrderEntity1.setDate(Date.from(now.toInstant().plus(1, ChronoUnit.HOURS)));

        UserOrderEntity userOrderEntity2 = new UserOrderEntity();
        userOrderEntity2.setUser(user1);
        userOrderEntity2.setDate(now);

        userOrderRepository.save(userOrderEntity);
        userOrderRepository.save(userOrderEntity1);
        userOrderRepository.save(userOrderEntity2);
    }

    @AfterEach
    void cleanUp() {
        assertDoesNotThrow(() -> {
            userOrderRepository.deleteAll();
            userRepository.deleteAll();

        }, "clean up should not error");
    }

    @Test
    void testFindByUserIdAndDate() {
        UserEntity user = userRepository.findFirstByEmail("user1@example.com").orElse(null);

        assertNotNull(user);

        List<UserOrderEntity> userOrderEntities = userOrderRepository.findByUserIdAndDate(user.getId(), now);

        assertEquals(2, userOrderEntities.size());

        userOrderEntities = userOrderRepository.findByUserIdAndDate(user.getId(),
                Date.from(now.toInstant().plus(1, ChronoUnit.HOURS)));

        assertEquals(1, userOrderEntities.size());
    }

    @Test
    void testFindUserOrders() {
        transactionOperations.executeWithoutResult(status -> {
            UserEntity user = userRepository.findFirstByEmail("user1@example.com").orElse(null);

            assertNotNull(user);

            assertEquals(3, user.getUserOrders().size());
        });
    }

    @Test
    void testFindByDate() {
        List<UserOrderEntity> userOrderEntities = userOrderRepository.findAllByDate(now);

        assertEquals(2, userOrderEntities.size());
    }

}
