package com.collins.Jwt_Auth_System.utils;

import com.collins.Jwt_Auth_System.enums.RoleType;
import com.collins.Jwt_Auth_System.model.Role;
import com.collins.Jwt_Auth_System.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        if (roleRepository.findByRoleName(RoleType.ROLE_USER).isEmpty()) {
            roleRepository.save(Role.builder()
                    .roleName(RoleType.ROLE_USER)
                    .description("Default user role")
                    .build());
        }

        if (roleRepository.findByRoleName(RoleType.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(Role.builder()
                    .roleName(RoleType.ROLE_ADMIN)
                    .description("Admin role")
                    .build());
        }
    }
}
