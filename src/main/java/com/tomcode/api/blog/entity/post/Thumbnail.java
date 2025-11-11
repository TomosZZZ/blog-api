package com.tomcode.api.blog.entity.post;

import com.tomcode.api.blog.utils.Base64Images;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class Thumbnail {
  @Column(nullable = false, columnDefinition = "TEXT")
  private String thumbnail;

  public Thumbnail(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Thumbnail cannot be empty");
    }
    if (!Base64Images.isValidImageBase64(value)) {
      throw new IllegalArgumentException(
          "Thumbnail must be a valid Base64 image (optionally as data URL)");
    }
    this.thumbnail = value;
  }
}
