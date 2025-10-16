package com.tomcode.api.blog.controllers;


import com.tomcode.api.blog.entity.user.User;
import com.tomcode.api.blog.exception.BadRequestException;
import com.tomcode.api.blog.exception.ForbiddenException;
import com.tomcode.api.blog.security.JWTParser;
import com.tomcode.api.blog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    UserService userService;
    JWTParser jwtParser;
    public UserController(UserService userService, JWTParser jwtParser) {
        this.userService = userService;
        this.jwtParser = jwtParser;
    }

    @GetMapping("/get")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader(value="Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Bad or missing authorization header");
        }
        if (!jwtParser.isAdmin(authHeader)) {
            throw new ForbiddenException("Access denied: admin role required");
        }

        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
