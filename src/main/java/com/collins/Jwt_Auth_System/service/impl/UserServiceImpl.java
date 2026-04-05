package com.collins.Jwt_Auth_System.service.impl;

import com.collins.Jwt_Auth_System.config.JwtService;
import com.collins.Jwt_Auth_System.dtos.ResponseDto;
import com.collins.Jwt_Auth_System.dtos.requests.LoginRequest;
import com.collins.Jwt_Auth_System.dtos.requests.UserCreationRequest;
import com.collins.Jwt_Auth_System.dtos.response.UserCreationResponse;
import com.collins.Jwt_Auth_System.dtos.response.UserLoginResponse;
import com.collins.Jwt_Auth_System.enums.RoleType;
import com.collins.Jwt_Auth_System.event.LoginCreatedEvent;
import com.collins.Jwt_Auth_System.event.UserCreatedEvent;
import com.collins.Jwt_Auth_System.exception.InvalidLoginException;
import com.collins.Jwt_Auth_System.exception.ResourceNotFoundException;
import com.collins.Jwt_Auth_System.exception.UserAlreadyExistException;
import com.collins.Jwt_Auth_System.mapper.UserMapper;
import com.collins.Jwt_Auth_System.model.Role;
import com.collins.Jwt_Auth_System.model.User;
import com.collins.Jwt_Auth_System.repository.RoleRepository;
import com.collins.Jwt_Auth_System.repository.UserRepository;
import com.collins.Jwt_Auth_System.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ApplicationEventPublisher publisher;


    @Override
    public ResponseEntity<ResponseDto<UserCreationResponse>> registerUser(UserCreationRequest creationRequest){
        log.info("Create user with email: {}", creationRequest.getEmail());

        if (userRepository.existsByEmail(creationRequest.getEmail())){
            throw new UserAlreadyExistException("User with email already exist " + creationRequest.getEmail());
        }

        User newUser = UserMapper.mapToUser(creationRequest);
        newUser.setPassword(passwordEncoder.encode(creationRequest.getPassword()));

        Role role = roleRepository.findByRoleName(RoleType.valueOf(RoleType.ROLE_USER.getRoleType()))
                .orElseThrow(() -> new ResourceNotFoundException("User role not found"));
        newUser.setRole(role);

        User savedUser = userRepository.save(newUser);
        publisher.publishEvent(new UserCreatedEvent(savedUser));

        UserCreationResponse response = UserMapper.mapToUserResponse(savedUser);
        ResponseDto<UserCreationResponse> userCreationResponseResponseDto = ResponseDto.<UserCreationResponse>builder()
                .statusCode("SUCCESS")
                .statusMessage("User registered")
                .data(response)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(userCreationResponseResponseDto);
    }

    @RateLimiter(name = "authLimiter", fallbackMethod = "loginRateLimitFallback")
    @Override
    public ResponseEntity<UserLoginResponse> loginUser(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email " + loginRequest.getEmail());
        }

        if (user.isAccountLocked()) {
            if (user.getLockTime() != null &&
                    user.getLockTime().plusMinutes(15).isBefore(LocalDateTime.now())) {
                user.setAccountLocked(false);
                user.setFailedLoginAttempts(0);
                user.setLockTime(null);
                userRepository.save(user);
            } else {
                throw new InvalidLoginException(
                        "Account is locked due to multiple failed login attempts. " +
                                "Please try again later or use the 'Forgot Password' option."
                );
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            user.setFailedLoginAttempts(0);
            userRepository.save(user);

            String jwtToken = jwtService.generateToken(loginRequest);

            UserLoginResponse loginResponse = new UserLoginResponse();
            loginResponse.setWelcomeMessage("Welcome " + authentication.getName() +
                    ", you have successfully logged in.");
            loginResponse.setToken(jwtToken);

            return ResponseEntity.ok(loginResponse);

        } catch (BadCredentialsException ex) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= 3) {
                user.setAccountLocked(true);
                user.setLockTime(LocalDateTime.now());
            }

            User savedLoginUser = userRepository.save(user);
            publisher.publishEvent(new LoginCreatedEvent(savedLoginUser));

            UserLoginResponse errorResponse = new UserLoginResponse();
            errorResponse.setWelcomeMessage("Invalid login credentials. Attempt " + attempts +
                    ". After 3 failed attempts, your account may be locked.");
            errorResponse.setToken(null);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    public ResponseEntity<UserLoginResponse> loginRateLimitFallback(LoginRequest loginRequest, Throwable t) {
        log.error("Rate limiter triggered for login: {}", t.getMessage());

        UserLoginResponse response = new UserLoginResponse();
        response.setWelcomeMessage("Too many requests. Please try again later.");
        response.setToken(null);

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
    }

    @Override
    public ResponseEntity<ResponseDto<User>> findByEmail(String email) {
        log.info("Fetching user by email: {}", email);

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email " + email);
        }

        ResponseDto<User> responseDto = ResponseDto.<User>builder()
                .statusCode("SUCCESS")
                .statusMessage("User retrieved successfully")
                .data(user)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Override
    public ResponseEntity<ResponseDto<List<User>>> getAllUsers() {
        log.info("Fetching all users");

        List<User> users = userRepository.findAll();

        ResponseDto<List<User>> responseDto = ResponseDto.<List<User>>builder()
                .statusCode("SUCCESS")
                .statusMessage("All users retrieved successfully")
                .data(users)
                .build();

        return ResponseEntity.ok(responseDto);
    }
}
