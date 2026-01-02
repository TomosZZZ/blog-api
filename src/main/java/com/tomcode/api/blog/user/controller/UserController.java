package com.tomcode.api.blog.user.controller;

import com.tomcode.api.blog.user.entity.RoleRequest;
import com.tomcode.api.blog.user.entity.User;
import com.tomcode.api.blog.user.entity.UserRole;
import com.tomcode.api.blog.user.service.UserService;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

  private final UserService userService;

  @GetMapping
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(
          @PathVariable UUID id,
          Authentication authentication) {

    userService.deleteUser(id, authentication.getName());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/role")
  public ResponseEntity<Void> updateUserRole(
          @PathVariable UUID id,
          @Valid @RequestBody RoleRequest request,
          Authentication authentication) {

    userService.updateUserRole(id, request.getRole(), authentication.getName());

    return ResponseEntity.noContent().build();
  }
}
