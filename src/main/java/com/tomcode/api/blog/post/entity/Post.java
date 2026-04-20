package com.tomcode.api.blog.post.entity;

import com.tomcode.api.blog.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "post")
public class Post {
  @Id
  @Column( updatable = false, nullable = false)
  private UUID id;

  @Embedded private Title title;

  @Setter @Embedded private Content content;

  @Setter @Embedded private Thumbnail thumbnail;

  @Embedded private Slug slug;

  @Setter
  @Enumerated(EnumType.STRING)
  @Column
  private PostStatus status;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reviewer_id")
  private User reviewer;

  @Setter
  @Column(name = "review_comment")
  private String reviewComment;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Setter
  @Column(name = "published_at")
  private LocalDateTime publishedAt;

  public Post(UUID id, Title title, Thumbnail thumbnail, Content content, User author) {
    this.id = id;
    this.slug = Slug.of(title.getTitle(), id);
    this.title = title;
    this.thumbnail = thumbnail;
    this.content = content;
    this.status = PostStatus.DRAFT;
    this.author = author;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public Post() {}

  @PreUpdate
  public void preUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public void setTitle(Title title) {
    this.title = title;
    this.slug = Slug.of(title.getTitle(), id);
  }
}
