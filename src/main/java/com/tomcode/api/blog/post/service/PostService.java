package com.tomcode.api.blog.post.service;

import com.tomcode.api.blog.common.exception.PostNotFoundException;
import com.tomcode.api.blog.post.dto.CreatePostDTO;
import com.tomcode.api.blog.post.dto.PostResponse;
import com.tomcode.api.blog.post.dto.UpdatePostDTO;
import com.tomcode.api.blog.post.entity.*;
import com.tomcode.api.blog.post.repository.PostRepository;
import com.tomcode.api.blog.user.entity.User;
import com.tomcode.api.blog.user.entity.UserRole;
import com.tomcode.api.blog.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {

  private final PostRepository postRepository;
  private final UserService userService;

  @Transactional
  public UUID createPost(CreatePostDTO createPostDTO, String userId, Boolean publishNow) {

    Title title = new Title(createPostDTO.getTitle());
    Content content = new Content(createPostDTO.getContent());
    Thumbnail thumbnail = new Thumbnail(createPostDTO.getThumbnail());
    UUID id = UUID.randomUUID();

    User author = userService.getUserById(userId);
    Post post = new Post(id, title, thumbnail, content, author);
    postRepository.save(post);

    if (author.hasRole(UserRole.ADMIN)) {
      post.setReviewer(author);
      if (publishNow) {
        post.setStatus(Status.PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());
      } else {
        post.setStatus(Status.REVIEWED);
      }
    }

    return post.getId();
  }

  @Transactional
  public void deletePost(UUID postId) {

    if (!postRepository.existsById(postId)) {
      throw new PostNotFoundException(postId);
    }
    postRepository.deleteById(postId);
  }

  public PostResponse getPostById(UUID id) {
    Optional<Post> postOptional = postRepository.findById(id);
    if (postOptional.isEmpty()) {
      throw new PostNotFoundException(id);
    }
    return toResponse(postOptional.get());
  }

  public List<PostResponse> getPosts() {
    return postRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional
  public void updatePost(UpdatePostDTO updatePostDTO, UUID postId) {

    Post post =
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

    Title title = new Title(updatePostDTO.getTitle());
    Content content = new Content(updatePostDTO.getContent());
    Thumbnail thumbnail = new Thumbnail(updatePostDTO.getThumbnail());

    post.setTitle(title);
    post.setContent(content);
    post.setThumbnail(thumbnail);

    postRepository.save(post);
  }

  private PostResponse toResponse(Post post) {
    return new PostResponse(
        post.getId().toString(),
        post.getTitle().getTitle(),
        post.getSlug().getSlug(),
        post.getThumbnail().getThumbnail(),
        post.getContent().getContent(),
        post.getCreatedAt().toString(),
        post.getUpdatedAt().toString());
  }
}
