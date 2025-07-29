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
        if(title.length()>50 || title.length()<3){
            throw new IllegalArgumentException("Title length must be between 3 and 50 characters");
        }
        if(thumbnail.isBlank()){
            throw new IllegalArgumentException("Thumbnail cannot be empty");
        }
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
    }
}
