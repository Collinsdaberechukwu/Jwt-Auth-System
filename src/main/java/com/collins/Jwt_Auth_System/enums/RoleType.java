package com.collins.Jwt_Auth_System.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleType {

    ADMIN("ADMIN"),
    USER("USER");

    private final String roleType;
}
