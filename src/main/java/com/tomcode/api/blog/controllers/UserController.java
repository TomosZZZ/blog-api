package com.tomcode.api.blog.controllers;

import com.tomcode.api.blog.entity.user.RoleRequest;
import com.tomcode.api.blog.entity.user.User;
import com.tomcode.api.blog.security.JWTParser;
import com.tomcode.api.blog.service.UserService;
import io.jsonwebtoken.Claims;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  UserService userService;
  JWTParser jwtParser;

  public UserController(UserService userService, JWTParser jwtParser) {
    this.userService = userService;
    this.jwtParser = jwtParser;
  }

  @GetMapping
  public ResponseEntity<List<User>> getAllUsers(
      @RequestHeader(value = "Authorization") String authHeader) {

    jwtParser.validateAdminPrivileges(authHeader);

    List<User> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<User> deleteUser(
      @PathVariable("id") String id, @RequestHeader(value = "Authorization") String authHeader) {
    jwtParser.validateAdminPrivileges(authHeader);

    Claims claims = jwtParser.getClaims(authHeader);

    userService.deleteUser(id, claims);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/role")
  public ResponseEntity<String> updateUserRole(
      @PathVariable("id") String id,
      @RequestBody RoleRequest role,
      @RequestHeader(value = "Authorization") String authHeader) {
    jwtParser.validateAdminPrivileges(authHeader);

    Claims claims = jwtParser.getClaims(authHeader);
    userService.updateUserRole(id, claims, role.getRole());
    return ResponseEntity.noContent().build();
  }
}
