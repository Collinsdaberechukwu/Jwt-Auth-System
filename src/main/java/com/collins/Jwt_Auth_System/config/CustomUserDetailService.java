package com.collins.Jwt_Auth_System.config;

import com.collins.Jwt_Auth_System.model.Role;
import com.collins.Jwt_Auth_System.model.User;
import com.collins.Jwt_Auth_System.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username);
        if (user == null){
            throw new UsernameNotFoundException("User not found with email :" + username);
        }

        return new CustomUserDetails(user);
    }
}
