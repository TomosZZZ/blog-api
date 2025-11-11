package com.tomcode.api.blog.exception;

import java.util.UUID;

public class PostNotFoundException extends ResourceNotFoundException {
  public PostNotFoundException(UUID id) {
    super("Post with id: " + id + " not found");
  }
}
