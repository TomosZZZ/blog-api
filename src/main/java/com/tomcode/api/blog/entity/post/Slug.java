package com.tomcode.api.blog.entity.post;

import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.text.Normalizer;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
public class Slug {
    private String slug;
    
    private Slug(String slug) {
        this.slug = slug;
    }

    public static Slug of(String title, UUID id){
        String slugifiedTitle = slugify(title);
        String slug = slugifiedTitle + "~" + id;
        return new Slug(slug);
    }

    private static String slugify(String input) {
        if(input == null || input.trim().equals("")){
            return "";
        }
        String nowhitespace = input.trim().replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("[^\\w\\-]", "").toLowerCase();
        return slug;
    }

}
