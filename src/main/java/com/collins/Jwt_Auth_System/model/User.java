package com.collins.Jwt_Auth_System.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class User extends BaseEntity{

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false,unique = true)
    private String password;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    private int failedLoginAttempts;

    private boolean accountLocked;

    private LocalDateTime lockTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    @ToString.Exclude
    private Role role;
}
