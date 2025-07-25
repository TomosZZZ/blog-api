package com.tomcode.api.blog.entity.post;

import com.tomcode.api.blog.util.SlugUtil;
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

    @Embedded
    private PostDetails details;

    @Column
    private String slug;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    public Post(PostDetails details) {
        this.details = details;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    public Post() {}

    public String getTitle() {
        return details.getTitle();
    }

    public String getContent() {
        return details.getContent();
    }

    public String getThumbnail() {
        return details.getThumbnail();
    }

    public void generateSlug(){
        if (this.slug == null && this.details != null) {
            String baseSlug = SlugUtil.toSlug(this.details.getTitle());
            this.slug = baseSlug + "-" + this.id;
        }
    }
}
