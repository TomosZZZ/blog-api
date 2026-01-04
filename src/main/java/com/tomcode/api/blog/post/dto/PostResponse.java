package com.tomcode.api.blog.post.dto;

import com.tomcode.api.blog.post.entity.Status;

public record PostResponse(
    String id,
    String title,
    String slug,
    String thumbnail,
    Status status,
    String content,
    String createdAt,
    String updatedAt) {}
