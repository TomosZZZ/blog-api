package com.tomcode.api.blog.post.dto;


import com.tomcode.api.blog.post.entity.PostStatus;

public record ChangeStatusDTO(PostStatus status,
                              String comment) { }
