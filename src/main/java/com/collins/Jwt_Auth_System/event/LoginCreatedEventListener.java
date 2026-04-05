package com.collins.Jwt_Auth_System.event;

import com.collins.Jwt_Auth_System.model.User;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoginCreatedEventListener implements ApplicationListener<@NonNull LoginCreatedEvent> {
    @Override
    public void onApplicationEvent(@NonNull LoginCreatedEvent event) {
        log.info("Event Created: {}", event);
        User user = (User) event.getSource();

        String WelcomeMessage = "Successfully logged in to your account: " + user.getUserName();
        log.info("SMS NOTIFICATION SENT: {} " , WelcomeMessage);

    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
