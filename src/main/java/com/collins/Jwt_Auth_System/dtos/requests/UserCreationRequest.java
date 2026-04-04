package com.collins.Jwt_Auth_System.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreationRequest {

    @NotBlank(message = "Username should not be null or empty")
    @Size(min = 3,max = 30)
    private String userName;

    @NotBlank(message = "User email should not be null or blank")
    @Email(message = "email is required")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 9, message = "password must be 9 characters")
    private String password;
}
