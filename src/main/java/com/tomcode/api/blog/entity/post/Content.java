package com.tomcode.api.blog.entity.post;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class Content {
    private String content;
    public Content(String content) {
        this.content = content;
    }
}
