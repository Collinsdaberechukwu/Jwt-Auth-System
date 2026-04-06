package com.collins.Jwt_Auth_System.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FetchUserRequest {
    @NotEmpty(message = "Email is required ")
    private String Email;
}
