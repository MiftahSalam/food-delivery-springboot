package com.example.fooddeliverysystem.service.user;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.fooddeliverysystem.entity.ConfirmationTokenEntity;
import com.example.fooddeliverysystem.entity.RoleEntity;
import com.example.fooddeliverysystem.entity.UserEntity;
import com.example.fooddeliverysystem.model.UserStatus;
import com.example.fooddeliverysystem.model.dto.UserDTO;
import com.example.fooddeliverysystem.model.dto.UserRegisterDTO;
import com.example.fooddeliverysystem.repository.ConfirmationTokenRepository;
import com.example.fooddeliverysystem.repository.RoleRepository;
import com.example.fooddeliverysystem.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private Environment environment;

    public String getConfirmationMailTItle() {
        return environment.getProperty("authentication.mailMessage.sendConfirmationMail.title");
    }

    public String getConfirmationMailBody() {
        return environment.getProperty("authentication.mailMessage.sendConfirmationMail.body");
    }

    public String getMailFooter() {
        return environment.getProperty("authentication.mailMessage.footer");
    }

    public String getResetPasswordMailTItle() {

        return environment.getProperty("authentication.mailMessage.sendResetPasswordMail.title");
    }

    public String getResetPasswordMailBody() {
        return environment.getProperty("authentication.mailMessage.sendResetPasswordMail.body");
    }

    public String getLoginUrl() {
        return getFrontendUrl() + environment.getProperty("authentication.url.login");
    }

    public String getResetPasswordUrl() {
        return getFrontendUrl() + environment.getProperty("authentication.resetPassword");
    }

    public String getFrontendUrl() {
        return environment.getProperty("frontend.url");
    }

    @Override
    public UserEntity registerUser(UserRegisterDTO user) {
        UserEntity existingUser = userRepository.findFirstByEmail(user.email()).orElse(null);
        if (existingUser != null) {
            return null;
        }

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setEmail(user.email());
        newUserEntity.setImagePath(user.imagePath());
        newUserEntity.setLastName(user.lastName());
        newUserEntity.setName(user.name());
        newUserEntity.setStatus(UserStatus.INACTIVE);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        newUserEntity.setPassword(encoder.encode(user.password()));

        RoleEntity userRole = roleRepository.findFirstByNameIgnoreCase("USER").orElse(null);
        newUserEntity.setRole(userRole);

        userRepository.save(newUserEntity);

        try {
            sendConfirmationMail(newUserEntity);
        } catch (Exception e) {
            System.out.println("error sending confirmation email"); // temporary
        }

        return newUserEntity;
    }

    @Override
    public UserEntity confirmUserAccount(String token) {
        ConfirmationTokenEntity confirmationTokenEntity = confirmationTokenRepository.findFirstByConfirmedToken(token)
                .orElse(null);
        if (confirmationTokenEntity != null) {
            UserEntity userEntity = userRepository
                    .findFirstByIdAndStatus(confirmationTokenEntity.getUser().getId(), UserStatus.INACTIVE)
                    .orElse(null);
            userEntity.setStatus(UserStatus.ACTIVE);
            userRepository.save(userEntity);

            confirmationTokenEntity.setConfirmDate(new Date());
            confirmationTokenRepository.save(confirmationTokenEntity);

            return userEntity;
        }

        return null;
    }

    @Override
    public UserDTO getUser(int id) {
        UserEntity userEntity = userRepository.findFirstByIdAndStatus((long) id, UserStatus.ACTIVE).orElse(null);
        if (userEntity == null) {
            return null;
        }

        return UserDTO.mapper(userEntity);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<UserDTO> results = new LinkedList<>();

        for (UserEntity user : userEntities) {
            if (user.getStatus() != UserStatus.DELETED && user.getStatus() != UserStatus.INACTIVE) {
                results.add(UserDTO.mapper(user));
            }
        }

        return results;
    }

    @Override
    public boolean updateUser(int id, String command) {
        UserEntity userEntity = userRepository.findById((long) id).orElse(null);
        if (userEntity == null) {
            return false;
        }

        System.out.println("Update user! id= " + id + " command= " + command);

        switch (command.toUpperCase()) {
            case "ACTIVE":
                userEntity.setStatus(UserStatus.ACTIVE);
                userRepository.save(userEntity);
                break;

            case "BAN":
                userEntity.setStatus(UserStatus.BANNED);
                userRepository.save(userEntity);
                break;

            case "DELETE":
                userEntity.setStatus(UserStatus.DELETED);
                userRepository.save(userEntity);
                break;

            case "PROMOTE":
                RoleEntity admin = roleRepository.findFirstByNameIgnoreCase("ADMIN").orElse(null);
                userEntity.setRole(admin);
                userRepository.save(userEntity);
                break;

            case "DEMOTION":
                RoleEntity userRole = roleRepository.findFirstByNameIgnoreCase("USER").orElse(null);
                userEntity.setRole(userRole);
                userRepository.save(userEntity);
                break;

            default:
                return false;
        }

        return true;
    }

    @Override
    public ResponseEntity<String> updateUserEmail(int id, String email) {
        if (id <= 0 || email == null) {
            return new ResponseEntity<>("id and email must not be null!", HttpStatus.BAD_REQUEST);
        }
        if (!userRepository.existsById((long) id)) {
            return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
        }

        UserEntity userEntity = userRepository.findFirstByEmail(email).orElse(null);
        if (userEntity != null) {
            return new ResponseEntity<>("email already exist", HttpStatus.BAD_REQUEST);
        }

        userEntity = userRepository.findById((long) id).orElse(null);
        userEntity.setEmail(email);
        userRepository.save(userEntity);

        try {
            sendConfirmationMail(userEntity);
        } catch (Exception e) {
            // return new ResponseEntity<String>("error sending confirmation email",
            // HttpStatus.INTERNAL_SERVER_ERROR);
            System.out.println("error sending confirmation email"); // temporary
        }

        return new ResponseEntity<String>("You need to verify your account again. Please check email", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> updateUserImage(int id, String imagePath) {
        if (id <= 0 || imagePath == null) {
            return new ResponseEntity<>("id and imagePath must not be null!", HttpStatus.BAD_REQUEST);
        }
        if (!userRepository.existsById((long) id)) {
            return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
        }

        UserEntity userEntity = userRepository.findById((long) id).orElse(null);
        userEntity.setImagePath(imagePath);
        userRepository.save(userEntity);

        return new ResponseEntity<String>("Image updated successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> updateUserPassword(String token, String password) {
        if (token == null || password == null) {
            return new ResponseEntity<>("token and password must not be null!", HttpStatus.BAD_REQUEST);
        }
        ConfirmationTokenEntity confirmationTokenEntity = confirmationTokenRepository.findFirstByConfirmedToken(token)
                .orElse(null);
        if (confirmationTokenEntity == null) {
            return new ResponseEntity<>("invalid token!", HttpStatus.UNAUTHORIZED);
        }

        UserEntity userEntity = userRepository.findById(confirmationTokenEntity.getUser().getId()).orElse(null);
        if (userEntity == null) {
            return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userEntity.setPassword(encoder.encode(password));
        userRepository.save(userEntity);

        return new ResponseEntity<String>("Password updated successfully", HttpStatus.OK);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findFirstByEmailAndStatus(email, UserStatus.ACTIVE).orElse(null);
        if (userEntity != null) {
            if (userEntity.getImagePath() != null) {
                userEntity.setImagePath(new String(Base64.decodeBase64(userEntity.getImagePath())));
            }

            return UserDTO.mapper(userEntity);
        }

        return null;
    }

    @Override
    public UserEntity resetPassoword(String email) {
        UserEntity userEntity = userRepository.findFirstByEmail(email).orElse(null);
        if (userEntity == null) {
            return null;
        } else {
            try {
                sendResetPasswordMail(userEntity);
            } catch (Exception e) {
                // return new ResponseEntity<String>("error sending confirmation email",
                // HttpStatus.INTERNAL_SERVER_ERROR);
                System.out.println("error sending confirmation email"); // temporary
            }
            return userEntity;
        }
    }

    private void sendResetPasswordMail(UserEntity userEntity) {
        ConfirmationTokenEntity confirmationTokenEntity = createConfirmationTokenEntity(userEntity);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        StringBuilder messageBodyBuilder = new StringBuilder();

        mailMessage.setTo(userEntity.getEmail());
        mailMessage.setSubject(getResetPasswordMailTItle());
        messageBodyBuilder.append(getResetPasswordMailBody());
        messageBodyBuilder.append(getResetPasswordUrl() + "?token=" + confirmationTokenEntity.getConfirmedToken());
        messageBodyBuilder.append(getMailFooter());
        mailMessage.setText(messageBodyBuilder.toString());

        emailSenderService.sendEmail(mailMessage);
    }

    private void sendConfirmationMail(UserEntity userEntity) {
        ConfirmationTokenEntity confirmationTokenEntity = createConfirmationTokenEntity(userEntity);
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(userEntity.getEmail());
        mailMessage.setSubject(getConfirmationMailTItle());

        StringBuilder messageBodyBuilder = new StringBuilder();
        messageBodyBuilder.append(getConfirmationMailBody());
        messageBodyBuilder.append(getLoginUrl() + "?token=" + confirmationTokenEntity.getConfirmedToken());
        messageBodyBuilder.append(getMailFooter());

        mailMessage.setText(messageBodyBuilder.toString());

        emailSenderService.sendEmail(mailMessage);
    }

    private ConfirmationTokenEntity createConfirmationTokenEntity(UserEntity userEntity) {
        ConfirmationTokenEntity confirmationTokenEntity = new ConfirmationTokenEntity();
        confirmationTokenEntity.setUser(userEntity);
        confirmationTokenEntity.setCreatedDate(new Date());
        confirmationTokenEntity.setConfirmedToken(generateToken());

        return confirmationTokenRepository.save(confirmationTokenEntity);
    }

    private String generateToken() {
        int n = 30;
        byte[] byteArray = new byte[256];
        new Random().nextBytes(byteArray);
        String randomString = new String(byteArray, StandardCharsets.UTF_8);
        StringBuilder builder = new StringBuilder();
        String alpanumericString = randomString.replaceAll("[^A-Za-z0-9]", "");

        for (int i = 0; i < alpanumericString.length(); i++) {
            if (Character.isLetter(alpanumericString.charAt(i)) && (n > 0)
                    || Character.isDigit(alpanumericString.charAt(i)) && (n > 0)) {
                builder.append(alpanumericString.charAt(i));
                n--;
            }
        }

        return builder.toString();
    }
}
