package com.collins.Jwt_Auth_System.mapper;

import com.collins.Jwt_Auth_System.dtos.requests.UserCreationRequest;
import com.collins.Jwt_Auth_System.dtos.response.UserCreationResponse;
import com.collins.Jwt_Auth_System.model.User;

import java.time.LocalDateTime;

public class UserMapper {

    public static User mapToUser(UserCreationRequest userRequest){

        User applicationUser = new User();
        applicationUser.setUserName(userRequest.getUserName());
        applicationUser.setEmail(userRequest.getEmail());
        applicationUser.setPassword(userRequest.getPassword());
        applicationUser.setLastLogin(LocalDateTime.now());
        return applicationUser;
    }

    public static UserCreationResponse mapToUserResponse(User applicationUser){
        UserCreationResponse userCreationResponse = new UserCreationResponse();
        userCreationResponse.setUserId(applicationUser.getId());
        userCreationResponse.setUserName(applicationUser.getUserName());
        userCreationResponse.setEmail(applicationUser.getEmail());

        userCreationResponse.setLastLogin(LocalDateTime.now());
        userCreationResponse.setCreatedAt(LocalDateTime.now());
        userCreationResponse.setUpdatedAt(applicationUser.getUpdatedAt());
        userCreationResponse.setRoleName(
                applicationUser.getRole() != null ? String.valueOf(applicationUser.getRole().getRoleName()) : null
        );
        return userCreationResponse;
    }
}
