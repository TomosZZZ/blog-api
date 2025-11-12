package com.tomcode.api.blog.post.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "post")
public class Post {
  @Id private UUID id;

  @Embedded private Title title;

  @Setter @Embedded private Content content;

  @Setter @Embedded private Thumbnail thumbnail;

  @Embedded private Slug slug;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public Post(Title title, Thumbnail thumbnail, Content content, UUID id) {
    this.id = id;
    this.slug = Slug.of(title.getTitle(), id);
    this.title = title;
    this.thumbnail = thumbnail;
    this.content = content;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public Post() {}

  public void setTitle(Title title) {
    this.title = title;
    this.slug = Slug.of(title.getTitle(), id);
  }
}
