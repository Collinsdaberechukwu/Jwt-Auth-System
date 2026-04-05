package com.collins.Jwt_Auth_System.event;

import com.collins.Jwt_Auth_System.model.User;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserCreatedEventListener implements ApplicationListener<@NonNull UserCreatedEvent> {

    @Override
    public void onApplicationEvent(@NonNull UserCreatedEvent event) {
        log.info("CREATED EVENT: {} ", event);
        User user = (User) event.getSource();

        String WellComeMessage = "Welcome to Jwt Auth System" + user.getUserName();
        log.info("SMS NOTIFICATION SENT: {} " , WellComeMessage);

    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
