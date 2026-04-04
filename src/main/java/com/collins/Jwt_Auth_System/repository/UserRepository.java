package com.collins.Jwt_Auth_System.repository;

import com.collins.Jwt_Auth_System.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String username);

    boolean existsByEmail(@NotBlank(message = "User email should not be null or blank") @Email(message = "email is required") String email);
}
