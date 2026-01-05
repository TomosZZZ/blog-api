package com.tomcode.api.blog.post.dto;

import com.tomcode.api.blog.post.entity.PostStatus;

import java.util.UUID;

public record PostPanelResponse(
        UUID id,
        String title,
        String content,
        String slug,
        String authorEmail,
        PostStatus status,
        String reviewComment,
        String createdAt
) {
}
