package com.tomcode.api.blog.controllers;

import com.tomcode.api.blog.dto.PostDTO;
import com.tomcode.api.blog.entity.post.Post;
import com.tomcode.api.blog.exception.PostNotFoundException;
import com.tomcode.api.blog.security.JWTParser;
import com.tomcode.api.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping(value = "/api/posts")
public class PostController {

    private JWTParser jwtParser;
    private PostService postService;

    public PostController(PostService postService, JWTParser jwtParser) {
        this.postService = postService;
        this.jwtParser = jwtParser;
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts =  postService.getPosts();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable UUID id) {
        Optional<Post> post = postService.getPostById(id);
        if(post.isEmpty()) {
            throw new PostNotFoundException(id);
        }
        return ResponseEntity.ok(post.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable UUID id,@RequestHeader(value="Authorization") String authHeader) {
        jwtParser.validateEditorOrAdminPrivileges(authHeader);

        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<String> createPost(@Valid @RequestBody PostDTO postDTO, @RequestHeader(value="Authorization") String authHeader) {

        jwtParser.validateEditorOrAdminPrivileges(authHeader);

        postService.createPost(postDTO);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updatePost(@PathVariable UUID id, @Valid @RequestBody PostDTO postDTO, @RequestHeader(value = "Authorization") String authHeader) {
        jwtParser.validateEditorOrAdminPrivileges(authHeader);
        postService.updatePost(postDTO, id);
        return ResponseEntity.noContent().build();
    }
}
