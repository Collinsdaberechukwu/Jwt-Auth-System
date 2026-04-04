package com.collins.Jwt_Auth_System.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResponseDto<T> {
    private String statusCode;
    private String statusMessage;
    private T data;
}
