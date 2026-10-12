package com.expensetracker;

import com.expensetracker.dto.LoginRequest;
import com.expensetracker.dto.RegisterRequest;
import com.expensetracker.entity.User;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExpenseTrackerApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void contextLoads() {
        assertNotNull(userRepository);
        assertNotNull(passwordEncoder);
        assertNotNull(jwtTokenProvider);
    }

    @Test
    void testUserRegistrationAndPasswordHashing() {
        String email = "testuser@example.com";
        String rawPassword = "password123";

        User user = new User("Test User", email, passwordEncoder.encode(rawPassword));
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertTrue(passwordEncoder.matches(rawPassword, savedUser.getPassword()));
        assertFalse(passwordEncoder.matches("wrongpassword", savedUser.getPassword()));
    }
}
