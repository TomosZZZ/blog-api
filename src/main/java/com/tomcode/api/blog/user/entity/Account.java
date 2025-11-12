package com.tomcode.api.blog.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "Account")
public class Account {

  @Id private String id;

  @Column(name = "user_id", unique = true, columnDefinition = "varchar(191)")
  private String userId;

  private String type;
  private String provider;

  @Column(name = "provider_account_id")
  private String providerAccountId;

  @Column(name = "refresh_token", columnDefinition = "TEXT")
  private String refreshToken;

  @Column(name = "access_token", columnDefinition = "TEXT")
  private String accessToken;

  @Column(name = "expires_at")
  private Integer expiresAt;

  @Column(name = "token_type")
  private String tokenType;

  private String scope;

  @Column(name = "id_token", columnDefinition = "TEXT")
  private String idToken;

  @Column(name = "session_state")
  private String sessionState;

  @Column(name = "refresh_token_expires_in")
  private Integer refreshTokenExpiresIn;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
  private User user;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
