package com.collins.Jwt_Auth_System.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class InvalidLoginException extends RuntimeException{

    public InvalidLoginException(String msg){
        super(msg);
    }
}
