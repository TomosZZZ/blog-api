package com.tomcode.api.blog.controllers;

import com.tomcode.api.blog.dto.PostDTO;
import com.tomcode.api.blog.entity.Post;
import com.tomcode.api.blog.security.JWTParser;
import com.tomcode.api.blog.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/posts")
public class PostController {

    private JWTParser jwtParser;
    private PostService postService;

    public PostController(PostService postService, JWTParser jwtParser) {
        this.postService = postService;
        this.jwtParser = jwtParser;
    }

    @GetMapping("/get")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts =  postService.getPosts();
        if(posts.isEmpty()) {
            return new ResponseEntity<>(posts, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String,String>> createPost(@RequestBody PostDTO postDTO, @RequestHeader(value="Authorization") String authHeader) {
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

        postService.createPost(postDTO);

        response.put("message", "Post created successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
