package com.collins.Jwt_Auth_System.service;

import com.collins.Jwt_Auth_System.dtos.ResponseDto;
import com.collins.Jwt_Auth_System.dtos.requests.LoginRequest;
import com.collins.Jwt_Auth_System.dtos.requests.UserCreationRequest;
import com.collins.Jwt_Auth_System.dtos.response.UserCreationResponse;
import com.collins.Jwt_Auth_System.dtos.response.UserLoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    ResponseEntity<ResponseDto<UserCreationResponse>> registerUser(UserCreationRequest creationRequest);

    ResponseEntity<UserLoginResponse> loginUser(LoginRequest loginRequest);
}
