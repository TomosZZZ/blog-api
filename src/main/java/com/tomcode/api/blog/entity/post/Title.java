package com.tomcode.api.blog.entity.post;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Embeddable
public class Title {
    @Column
    private String title;

    public Title(String title) {
        if (title == null) {
            throw new NullPointerException("Title cannot be null");
        }

        if(title.length()>50 || title.length()<3){
            throw new IllegalArgumentException("Title length must be between 3 and 50 characters");
        }
        this.title = title;
    }
}
