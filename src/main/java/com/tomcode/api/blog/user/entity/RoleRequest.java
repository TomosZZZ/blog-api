package com.tomcode.api.blog.user.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {
  @NotBlank
  private String role;
}
