package com.tomcode.api.blog.entity.post;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Getter;

@Getter
@Embeddable
public class PostDetails {
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Lob
    private String thumbnail;

    protected PostDetails() {}

    public PostDetails(String title, String content, String thumbnail) {


        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
    }
}
