package com.collins.Jwt_Auth_System.repository;

import com.collins.Jwt_Auth_System.enums.RoleType;
import com.collins.Jwt_Auth_System.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(RoleType roleName);

}
