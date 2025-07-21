package com.tomcode.api.blog.entity;

import com.tomcode.api.blog.dto.PostDTO;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Lob
    @Column(name="thumbnail", columnDefinition = "MEDIUMTEXT")
    private String thumbnail;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    public Post(PostDTO postDTO) {
        this.title = postDTO.getTitle();
        this.content = postDTO.getContent();
        this.thumbnail = postDTO.getThumbnail();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    public Post() {}
}
