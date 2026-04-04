package com.collins.Jwt_Auth_System.service.impl;

import com.collins.Jwt_Auth_System.config.JwtService;
import com.collins.Jwt_Auth_System.dtos.ResponseDto;
import com.collins.Jwt_Auth_System.dtos.requests.LoginRequest;
import com.collins.Jwt_Auth_System.dtos.requests.UserCreationRequest;
import com.collins.Jwt_Auth_System.dtos.response.UserCreationResponse;
import com.collins.Jwt_Auth_System.dtos.response.UserLoginResponse;
import com.collins.Jwt_Auth_System.enums.RoleType;
import com.collins.Jwt_Auth_System.event.UserCreatedEvent;
import com.collins.Jwt_Auth_System.exception.ResourceNotFoundException;
import com.collins.Jwt_Auth_System.exception.UserAlreadyExistException;
import com.collins.Jwt_Auth_System.mapper.UserMapper;
import com.collins.Jwt_Auth_System.model.Role;
import com.collins.Jwt_Auth_System.model.User;
import com.collins.Jwt_Auth_System.repository.RoleRepository;
import com.collins.Jwt_Auth_System.repository.UserRepository;
import com.collins.Jwt_Auth_System.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        Role role = roleRepository.findByRoleName(RoleType.valueOf(RoleType.USER.getRoleType()))
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

    @Override
    public ResponseEntity<UserLoginResponse> loginUser(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword())
        );

        if (authentication.isAuthenticated()){
            String jwtToken = jwtService.generateToken(loginRequest);

            UserLoginResponse loginResponse = new UserLoginResponse();
            loginResponse.setWelcomeMessage("Welcome " + authentication.getName() + ", you have successfully logged in.");
            loginResponse.setToken(jwtToken);

            return ResponseEntity.ok(loginResponse);
    }else {
            throw new ResourceNotFoundException("Invalid login credentials for email " + loginRequest.getEmail());
        }
    }
}
