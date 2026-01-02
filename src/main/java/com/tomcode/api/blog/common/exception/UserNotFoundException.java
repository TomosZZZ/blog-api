package com.tomcode.api.blog.common.exception;

public class UserNotFoundException extends ResourceNotFoundException {
  public UserNotFoundException(String id) {
    super("User not found");
  }
}
