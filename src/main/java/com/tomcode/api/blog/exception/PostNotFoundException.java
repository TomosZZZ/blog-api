package com.tomcode.api.blog.exception;

public class PostNotFoundException extends ResourceNotFoundException {
    public PostNotFoundException(String id) {
      super("Post with id: " + id + " not found");
    }
}
