package com.tomcode.api.blog.controllers;

import com.tomcode.api.blog.dto.PostDTO;
import com.tomcode.api.blog.entity.post.Post;
import com.tomcode.api.blog.exception.ForbiddenException;
import com.tomcode.api.blog.exception.BadRequestException;
import com.tomcode.api.blog.exception.PostNotFoundException;
import com.tomcode.api.blog.security.JWTParser;
import com.tomcode.api.blog.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


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
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable String id) {
        Optional<Post> post = postService.getPostById(id);
        if(post.isEmpty()) {
            throw new PostNotFoundException(id);
        }
        return ResponseEntity.ok(post.get());
    }

    @PostMapping("/create")
    public ResponseEntity createPost(@RequestBody PostDTO postDTO, @RequestHeader(value="Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Bad or missing authorization header");
        }
        if (!jwtParser.isAdmin(authHeader)) {
            throw new ForbiddenException("Access denied: admin role required");
        }

        postService.createPost(postDTO);

        return ResponseEntity.ok().build();

    }
}
