package com.tomcode.api.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatePostDTO {
  @NotBlank(message = "Title must not be empty")
  @Size(min = 3, max = 50, message = "Title must be between 3 an 50 characters")
  private String title;

  private String content;

  @NotBlank(message = "Thumbnail must not be empty")
  private String thumbnail;
}
