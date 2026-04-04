package com.collins.Jwt_Auth_System.model;

import com.collins.Jwt_Auth_System.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Table(name = "roles",uniqueConstraints = @UniqueConstraint(columnNames = "role_name"))
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role extends BaseEntity{

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    private RoleType roleName;

    @OneToMany(mappedBy = "role")
    private List<User> users;
}
