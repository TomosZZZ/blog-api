package com.tomcode.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    @Column(name="user_id")
    private String userId;

    @Column(name = "type")
    private String type;

    @Column(name = "provider")
    private String provider;

    @Column(name="provider_account_id")
    private String providerAccountId;

    @Column(name = "refresh_token")
    private String refresh_token;

    @Column(name = "access_token")
    private String access_token;

    @Column(name = "expires_at")
    private int expires_at;

    @Column(name = "token_type")
    private String token_type;

    @Column(name = "scope")
    private String scope;

    @Column(name = "id_token")
    private String id_token;

    @Column(name = "session_state")
    private String session_state;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private User user;


}
