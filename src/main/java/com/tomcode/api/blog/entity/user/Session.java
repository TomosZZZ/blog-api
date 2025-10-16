package com.tomcode.api.blog.entity.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Session")
public class Session {

    @Id
    private String id;

    @Column(name = "session_token", unique = true)
    private String sessionToken;

    @Column(name = "user_id", columnDefinition = "varchar(191)")
    private String userId;

    private LocalDateTime expires;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

