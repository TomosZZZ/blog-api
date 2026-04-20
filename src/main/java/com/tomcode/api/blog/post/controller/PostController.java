package com.tomcode.api.blog.post.controller;

import com.tomcode.api.blog.post.dto.*;
import com.tomcode.api.blog.post.service.PostLifecycleService;
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
  private final PostLifecycleService postLifecycleService;


  @GetMapping
  public ResponseEntity<List<PostResponse>> getAllPosts() {
    return ResponseEntity.ok(postService.getPosts());
  }

  @GetMapping("/panel")
  @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
  public ResponseEntity<List<PostPanelResponse>> getPostsForPanel(Authentication auth,@RequestParam(required = false, defaultValue = "ALL") String scope) {
    String email = auth.getName();

    return ResponseEntity.ok(postService.getPostsForPanel(email,scope));
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
            .body(Map.of("id", id));
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

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
  public ResponseEntity<Void> changePostStatus(
          @PathVariable UUID id,
          @Valid @RequestBody ChangeStatusDTO dto,
          Authentication authentication
  ) {
    postLifecycleService.changeStatus(
            dto,
            authentication.getName(),
            id
    );
    return ResponseEntity.noContent().build();
  }
}

