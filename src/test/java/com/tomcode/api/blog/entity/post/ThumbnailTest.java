package com.tomcode.api.blog.entity.post;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ThumbnailTest {
    @Test
    void shouldCreateThumbnailWithValidBase64() {
        // given
        String validBase64 = "aGVsbG8=";

        // when
        Thumbnail thumbnail = new Thumbnail(validBase64);

        // then
        assertEquals(validBase64, thumbnail.getThumbnail());
    }

    @Test
    void shouldThrowExceptionWhenEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Thumbnail(""));
    }

    @Test
    void shouldThrowExceptionWhenInvalidBase64() {
        String invalidBase64 = "not@base64!!";
        assertThrows(IllegalArgumentException.class, () -> new Thumbnail(invalidBase64));
    }
}
