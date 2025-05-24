package com.tomcode.api.blog.dto;

import lombok.Data;

@Data
public class PostDTO {
    private String title;
    private String content;
    private String thumbnail;
}
