package com.example.fooddeliverysystem.security;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@SpringBootTest
public class TokenUtilsTest {
    @Autowired
    private TokenUtils tokenUtils;

    @Test
    void testGenerateToken() {
        List<GrantedAuthority> grantedAuthoritiesUser = new ArrayList<>();
        grantedAuthoritiesUser.add(new SimpleGrantedAuthority("ADMIN"));
        User userDetails = new User("test@example.com", "rahasia", grantedAuthoritiesUser);

        String token = tokenUtils.generateToken(userDetails);
        assertNotNull(token);
    }
}
