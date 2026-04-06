package com.collins.Jwt_Auth_System;

import com.collins.Jwt_Auth_System.dtos.requests.LoginRequest;
import com.collins.Jwt_Auth_System.dtos.requests.UserCreationRequest;
import com.collins.Jwt_Auth_System.enums.RoleType;
import com.collins.Jwt_Auth_System.model.Role;
import com.collins.Jwt_Auth_System.model.User;
import com.collins.Jwt_Auth_System.repository.RoleRepository;
import com.collins.Jwt_Auth_System.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setup() {
        if (roleRepository.findByRoleName(RoleType.valueOf("ROLE_USER")).isEmpty()) {
            Role role = new Role();
            role.setRoleName(RoleType.valueOf("ROLE_USER"));
            role.setDescription("Default user role");
            roleRepository.save(role);
        }
    }

    // ✅ 1. Register User Test
    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        UserCreationRequest request = new UserCreationRequest();
        request.setEmail("collinstest@gmail.com");
        request.setUserName("Collins Test User");
        request.setPassword("password123");

        mockMvc.perform(post("/api/v1/users/register_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.statusCode").value("SUCCESS"))
                .andExpect((ResultMatcher) jsonPath("$.data.email").value("collinstest@gmail.com"));
    }

    //  2. Failed Login Attempt Increments Counter
    @Test
    void shouldIncreaseFailedAttempts_onInvalidLogin() throws Exception {

        User user = new User();
        user.setEmail("fail@gmail.com");
        user.setUserName("Fail User");
        user.setPassword(passwordEncoder.encode("correctPassword"));
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);

        Role role = roleRepository.findByRoleName(RoleType.valueOf("ROLE_USER")).orElseThrow();
        user.setRole(role);

        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("fail@gmail.com");
        request.setPassword("wrongPassword");

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());

        User updated = userRepository.findByEmail("fail@gmail.com");
        assertEquals(1, updated.getFailedLoginAttempts());
    }

    // 🔒 3. Lock Account After 3 Attempts
    @Test
    void shouldLockAccount_afterThreeFailedAttempts() throws Exception {

        User user = new User();
        user.setEmail("lock@gmail.com");
        user.setUserName("Lock User");
        user.setPassword(passwordEncoder.encode("correctPassword"));
        user.setFailedLoginAttempts(2);
        user.setAccountLocked(false);

        Role role = roleRepository.findByRoleName(RoleType.valueOf("ROLE_USER")).orElseThrow();
        user.setRole(role);

        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("lock@gmail.com");
        request.setPassword("wrongPassword");

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());

        User updated = userRepository.findByEmail("lock@gmail.com");

        assertTrue(updated.isAccountLocked());
        assertEquals(3, updated.getFailedLoginAttempts());
    }

    @Test
    void shouldResetAttempts_onSuccessfulLogin() throws Exception {

        User user = new User();
        user.setEmail("collinssuccess@gmail.com");
        user.setUserName("Success User");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFailedLoginAttempts(2);
        user.setAccountLocked(false);

        Role role = roleRepository.findByRoleName(RoleType.valueOf("ROLE_USER")).orElseThrow();
        user.setRole(role);

        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("collinssuccess@gmail.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.token").exists());

        User updated = userRepository.findByEmail("collinssuccess@gmail.com");

        assertEquals(0, updated.getFailedLoginAttempts());
    }
}
