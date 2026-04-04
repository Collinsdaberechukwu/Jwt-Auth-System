package com.collins.Jwt_Auth_System.dtos.response;

import lombok.Data;

@Data
public class UserLoginResponse {

    private String welcomeMessage;
    private String token;
}
