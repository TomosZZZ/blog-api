package com.tomcode.api.blog.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;
}
