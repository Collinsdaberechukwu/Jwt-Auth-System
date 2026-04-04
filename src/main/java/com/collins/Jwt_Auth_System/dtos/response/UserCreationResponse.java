package com.collins.Jwt_Auth_System.dtos.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCreationResponse {

    private Long userId;
    private String userName;
    private String email;
    private LocalDateTime lastLogin;

    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
