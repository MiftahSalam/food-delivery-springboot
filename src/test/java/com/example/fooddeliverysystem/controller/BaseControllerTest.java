package com.example.fooddeliverysystem.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.model.UserStatus;
import com.example.fooddeliverysystem.repository.RoleRepository;
import com.example.fooddeliverysystem.repository.UserRepository;
import com.example.fooddeliverysystem.security.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseControllerTest {
    @Autowired
    protected UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TokenUtils tokenUtils;

    protected String adminToken;

    protected String userToken;

    void setUp() {
        UserEntity admin = new UserEntity();
        admin.setChoosenOne(false);
        admin.setEmail("admin@example.com");
        admin.setName("admin");
        admin.setLastName("super");
        admin.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        admin.setStatus(UserStatus.ACTIVE);
        admin.setRole(roleRepository.findFirstByNameIgnoreCase("ADMIN").get());

        UserEntity user = new UserEntity();
        user.setChoosenOne(false);
        user.setEmail("user@example.com");
        user.setName("user1");
        user.setLastName("test");
        user.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(roleRepository.findFirstByNameIgnoreCase("USER").get());

        admin = userRepository.save(admin);
        user = userRepository.save(user);

        List<GrantedAuthority> grantedAuthoritiesAdmin = new ArrayList<>();
        grantedAuthoritiesAdmin.add(new SimpleGrantedAuthority(admin.getRole().getName()));
        User adminDetails = new User(admin.getEmail(), admin.getPassword(), grantedAuthoritiesAdmin);

        adminToken = tokenUtils.generateToken(adminDetails);

        List<GrantedAuthority> grantedAuthoritiesUser = new ArrayList<>();
        grantedAuthoritiesUser.add(new SimpleGrantedAuthority(user.getRole().getName()));
        User userDetails = new User(user.getEmail(), user.getPassword(), grantedAuthoritiesUser);

        userToken = tokenUtils.generateToken(userDetails);

    }

    void cleanUp() {
        userRepository.deleteAll();
    }
}
