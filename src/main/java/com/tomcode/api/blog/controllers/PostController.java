package com.tomcode.api.blog.controllers;

import com.tomcode.api.blog.entity.Post;
import com.tomcode.api.blog.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/post")
public class PostController {

    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    public void createPost(@RequestBody Post post) {
        postService.createPost(post);
        ResponseEntity.ok("Post created successfully");

    }
}
