package com.example.fooddeliverysystem.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.model.UserStatus;
import com.example.fooddeliverysystem.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findFirstByEmail(username).orElse(null);
        if (userEntity == null || userEntity.getStatus() == UserStatus.INACTIVE) {
            throw new UsernameNotFoundException(String.format("user not found with username: '%s'.", username));
        } else {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(new SimpleGrantedAuthority(userEntity.getRole().getName()));

            return new User(userEntity.getEmail(), userEntity.getPassword(), grantedAuthorities);
        }
    }

}
