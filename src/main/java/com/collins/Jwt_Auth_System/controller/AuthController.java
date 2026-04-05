package com.collins.Jwt_Auth_System.controller;

import com.collins.Jwt_Auth_System.dtos.ResponseDto;
import com.collins.Jwt_Auth_System.dtos.requests.LoginRequest;
import com.collins.Jwt_Auth_System.dtos.requests.UserCreationRequest;
import com.collins.Jwt_Auth_System.dtos.response.UserCreationResponse;
import com.collins.Jwt_Auth_System.dtos.response.UserLoginResponse;
import com.collins.Jwt_Auth_System.model.User;
import com.collins.Jwt_Auth_System.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/public/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Application is running");
    }

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

    @GetMapping("/user/me")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDto<User>> getUser(Authentication authentication) {
        String email = authentication.getDeclaringClass().getName();

        ResponseEntity<ResponseDto<User>> userResponse = userService.findByEmail(email);
        return userResponse;
    }


    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<List<User>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().getBody());
    }
}
