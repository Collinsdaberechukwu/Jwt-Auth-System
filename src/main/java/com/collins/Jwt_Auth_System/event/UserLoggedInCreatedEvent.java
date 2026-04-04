package com.collins.Jwt_Auth_System.event;

import org.springframework.context.ApplicationEvent;

public class UserLoggedInCreatedEvent extends ApplicationEvent {
    public UserLoggedInCreatedEvent(Object source) {
        super(source);
    }
}
