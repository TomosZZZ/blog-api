package com.tomcode.api.blog.exception;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String id) {
        super("User with id " + id + " not found");
    }
}
