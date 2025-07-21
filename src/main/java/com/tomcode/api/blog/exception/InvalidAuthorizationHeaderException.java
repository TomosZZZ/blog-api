package com.tomcode.api.blog.exception;

public class InvalidAuthorizationHeaderException extends RuntimeException {
    public InvalidAuthorizationHeaderException() {
        super("Invalid or missing Authorization header");
    }
}
