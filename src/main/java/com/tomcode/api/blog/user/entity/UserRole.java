package com.tomcode.api.blog.user.entity;

public enum UserRole {
  USER,
  ADMIN,
  EDITOR;

  public static boolean includes(String value) {
    try {
      UserRole.valueOf(value.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
