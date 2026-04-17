package com.tomcode.api.blog.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class Content {
    @Column(columnDefinition = "TEXT")
    private String content;
    public Content(String content) {
        this.content = content;
    }
}
