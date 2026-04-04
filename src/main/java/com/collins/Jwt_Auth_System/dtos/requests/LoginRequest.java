package com.collins.Jwt_Auth_System.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotEmpty(message = "Email is required for login")
    private String email;

    @NotEmpty(message = "Password ir required")
    private String password;
}
