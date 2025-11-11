package com.tomcode.api.blog.dto;

public record PostResponse(
    String id,
    String title,
    String slug,
    String thumbnail,
    String content,
    String createdAt,
    String updatedAt) {}
