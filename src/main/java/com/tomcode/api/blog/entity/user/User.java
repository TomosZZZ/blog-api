package com.tomcode.api.blog.entity.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "User")
public class User {

    @Id
    @Column(columnDefinition = "varchar(191)")
    private String id;

    private String name;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(name = "email_verified")
    private LocalDateTime emailVerified;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    private String password;
    private String image;

    @OneToMany(mappedBy = "user")
    private List<Account> accounts;

    @OneToMany(mappedBy = "user")
    private List<Session> sessions;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
