package com.collins.Jwt_Auth_System.controller;

import com.collins.Jwt_Auth_System.dtos.ResponseDto;
import com.collins.Jwt_Auth_System.dtos.requests.FetchUserRequest;
import com.collins.Jwt_Auth_System.dtos.requests.LoginRequest;
import com.collins.Jwt_Auth_System.dtos.requests.UserCreationRequest;
import com.collins.Jwt_Auth_System.dtos.response.UserCreationResponse;
import com.collins.Jwt_Auth_System.dtos.response.UserLoginResponse;
import com.collins.Jwt_Auth_System.model.User;
import org.springframework.security.core.Authentication;
import com.collins.Jwt_Auth_System.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/public_health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Application is running");
    }

    @PostMapping("/register_user")
    public ResponseEntity<ResponseDto<UserCreationResponse>> registerUser(
                        @RequestBody @Valid UserCreationRequest creationRequest){
        return userService.registerUser(creationRequest);
    }


    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<UserCreationResponse>> registerAdmin(
                                                            @Valid @RequestBody UserCreationRequest request) {
        return userService.registerAdmin(request);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest){
        UserLoginResponse response = userService.loginUser(loginRequest).getBody();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/me")
    public ResponseEntity<ResponseDto<UserCreationResponse>> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        FetchUserRequest request = new FetchUserRequest();
        request.setEmail(email);
        return userService.findByEmail(request);
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<List<UserCreationResponse>>> getAllUsers() {
        return userService.getAllUsers();
    }


}
