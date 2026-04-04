package com.collins.Jwt_Auth_System.event;

import com.collins.Jwt_Auth_System.model.User;
import org.springframework.context.ApplicationEvent;

public class UserCreatedEvent extends ApplicationEvent {
    public UserCreatedEvent(User user) {
        super(user);
    }
}
