package com.tomcode.api.blog.post.dto;

import com.tomcode.api.blog.post.entity.PostStatus;

public record PostResponse(
    String id,
    String title,
    String slug,
    String thumbnail,
    String authorEmail,
    PostStatus status,
    String content,
    String createdAt,
    String updatedAt) {}
