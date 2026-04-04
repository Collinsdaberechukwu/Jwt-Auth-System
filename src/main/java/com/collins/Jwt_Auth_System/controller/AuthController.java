package com.collins.Jwt_Auth_System.controller;

import com.collins.Jwt_Auth_System.dtos.ResponseDto;
import com.collins.Jwt_Auth_System.dtos.requests.LoginRequest;
import com.collins.Jwt_Auth_System.dtos.requests.UserCreationRequest;
import com.collins.Jwt_Auth_System.dtos.response.UserCreationResponse;
import com.collins.Jwt_Auth_System.dtos.response.UserLoginResponse;
import com.collins.Jwt_Auth_System.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register_user")
    public ResponseEntity<ResponseDto<UserCreationResponse>> registerUser(
                        @RequestBody @Valid UserCreationRequest creationRequest){
        return userService.registerUser(creationRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest){
        UserLoginResponse response = userService.loginUser(loginRequest).getBody();
        return ResponseEntity.ok(response);
    }
}
