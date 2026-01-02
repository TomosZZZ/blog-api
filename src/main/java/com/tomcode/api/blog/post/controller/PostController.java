package com.tomcode.api.blog.post.controller;

import com.tomcode.api.blog.post.dto.CreatePostDTO;
import com.tomcode.api.blog.post.dto.PostResponse;
import com.tomcode.api.blog.post.dto.UpdatePostDTO;
import com.tomcode.api.blog.post.service.PostService;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {

  private final PostService postService;

  @GetMapping
  public ResponseEntity<List<PostResponse>> getAllPosts() {
    return ResponseEntity.ok(postService.getPosts());
  }

  @GetMapping("/{id}")
  public ResponseEntity<PostResponse> getPostById(@PathVariable UUID id) {
    return ResponseEntity.ok(postService.getPostById(id));
  }

  @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<?> createPost(
          @Valid @RequestBody CreatePostDTO dto,
          @RequestParam(defaultValue = "false") boolean publishNow,
          Authentication authentication) {

    UUID id = postService.createPost(
            dto,
            authentication.getName(),
            publishNow
    );

    return ResponseEntity
            .created(URI.create("/api/posts/" + id))
            .body(Map.of("message", "Post created"));
  }

  @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
  @PatchMapping("/{id}")
  public ResponseEntity<Void> updatePost(
          @PathVariable UUID id,
          @Valid @RequestBody UpdatePostDTO dto,
          Authentication authentication) {

    postService.updatePost(id, dto, authentication.getName());
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePost(
          @PathVariable UUID id,
          Authentication authentication) {

    postService.deletePost(id, authentication.getName());
    return ResponseEntity.noContent().build();
  }
}

