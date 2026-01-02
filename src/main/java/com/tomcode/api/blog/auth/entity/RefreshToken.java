package com.tomcode.api.blog.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true, length = 64, name = "token_hash")
  private String tokenHash;

//  @ManyToOne(fetch = FetchType.LAZY,optional = false)
//  @JoinColumn(name = "user_id", nullable = false)
//  private UserDev user;
@Column(nullable = false)
private String userEmail;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "expires_at")
  private Instant expiresAt;

  @Column(name = "revoked_at")
  private Instant revokedAt;

  @OneToOne
  @JoinColumn(name = "rotated_to_id")
  private RefreshToken rotatedTo;
}
