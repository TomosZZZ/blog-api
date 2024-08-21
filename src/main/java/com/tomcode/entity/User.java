package com.tomcode.entity;

import com.tomcode.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name="name")
    private String name;

    @Column(name="email" )
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="email_verified")
    private Timestamp emailVerified;

    @Column(name="role")
    private Role role;

    @Column(name="image")
    private String image;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Account> accounts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Session> sessions;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<VerificationToken> verificationTokens;
}
