package com.tomcode.api.blog.controllers;

import com.tomcode.api.blog.entity.Post;
import com.tomcode.api.blog.security.JWTParser;
import com.tomcode.api.blog.service.PostService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/post")
public class PostController {

    private JWTParser jwtParser;
    private PostService postService;

    public PostController(PostService postService, JWTParser jwtParser) {
        this.postService = postService;
        this.jwtParser = jwtParser;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String,String>> createPost(@RequestBody Post post, @RequestHeader(value="Authorization") String authHeader) {
        Map<String, String> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("message", "Invalid or missing Authorization header");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.replace("Bearer ", "");

        if(!jwtParser.isAdmin(token)){
            response.put("message", "Access denied");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        postService.createPost(post);

        response.put("message", "Post created successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
