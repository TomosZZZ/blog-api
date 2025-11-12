package com.tomcode.api.blog.post.controller;

import com.tomcode.api.blog.post.dto.CreatePostDTO;
import com.tomcode.api.blog.post.dto.PostResponse;
import com.tomcode.api.blog.post.dto.UpdatePostDTO;
import com.tomcode.api.blog.security.JWTParser;
import com.tomcode.api.blog.post.service.PostService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/posts")
public class PostController {

  private final JWTParser jwtParser;
  private final PostService postService;

  @GetMapping
  public ResponseEntity<List<PostResponse>> getAllPosts() {
    List<PostResponse> posts = postService.getPosts();
    return new ResponseEntity<>(posts, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PostResponse> getPostById(@PathVariable UUID id) {
    PostResponse post = postService.getPostById(id);

    return ResponseEntity.ok(post);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deletePost(
      @PathVariable UUID id, @RequestHeader(value = "Authorization") String authHeader) {
    jwtParser.validateEditorOrAdminPrivileges(authHeader);

    postService.deletePost(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping
  public ResponseEntity<String> createPost(
      @Valid @RequestBody CreatePostDTO createPostDTO,
      @RequestHeader(value = "Authorization") String authHeader,
      @RequestParam(defaultValue = "false") boolean publishNow) {

    jwtParser.validateEditorOrAdminPrivileges(authHeader);

    Claims claims = jwtParser.getClaims(authHeader);
    String authorId = claims.get("sub", String.class);

    postService.createPost(createPostDTO, authorId, publishNow);

    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{id}")
  public ResponseEntity<String> updatePost(
      @PathVariable UUID id,
      @Valid @RequestBody UpdatePostDTO updatePostDTO,
      @RequestHeader(value = "Authorization") String authHeader) {
    jwtParser.validateEditorOrAdminPrivileges(authHeader);
    postService.updatePost(updatePostDTO, id);
    return ResponseEntity.noContent().build();
  }
}
