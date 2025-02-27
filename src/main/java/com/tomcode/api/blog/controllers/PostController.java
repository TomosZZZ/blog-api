package com.tomcode.api.blog.controllers;

import com.tomcode.api.blog.entity.Post;
import com.tomcode.api.blog.service.PostService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/post")
public class PostController {

    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity createPost(@RequestBody Post post) {
        postService.createPost(post);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post created successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
