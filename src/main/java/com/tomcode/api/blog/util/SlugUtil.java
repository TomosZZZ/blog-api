package com.tomcode.api.blog.util;

import java.text.Normalizer;

public class SlugUtil {
    public static String toSlug(String input) {
        if(input == null || input.trim().equals("")){
            return "";
        }
        String nowhitespace = input.trim().replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("[^\\w\\-]", "").toLowerCase();
        return slug;
    }
}
