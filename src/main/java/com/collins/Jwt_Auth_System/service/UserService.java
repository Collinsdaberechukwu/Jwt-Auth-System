package com.collins.Jwt_Auth_System.service;

import com.collins.Jwt_Auth_System.dtos.ResponseDto;
import com.collins.Jwt_Auth_System.dtos.requests.LoginRequest;
import com.collins.Jwt_Auth_System.dtos.requests.UserCreationRequest;
import com.collins.Jwt_Auth_System.dtos.response.UserCreationResponse;
import com.collins.Jwt_Auth_System.dtos.response.UserLoginResponse;
import com.collins.Jwt_Auth_System.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    ResponseEntity<ResponseDto<UserCreationResponse>> registerUser(UserCreationRequest creationRequest);

    ResponseEntity<UserLoginResponse> loginUser(LoginRequest loginRequest);

    ResponseEntity<ResponseDto<User>> findByEmail(String email);

    ResponseEntity<ResponseDto<List<User>>> getAllUsers();
}
