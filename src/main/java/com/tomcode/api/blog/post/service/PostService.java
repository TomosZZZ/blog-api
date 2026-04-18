package com.tomcode.api.blog.post.service;

import com.tomcode.api.blog.common.exception.ForbiddenException;
import com.tomcode.api.blog.common.exception.PostNotFoundException;
import com.tomcode.api.blog.post.dto.CreatePostDTO;
import com.tomcode.api.blog.post.dto.PostPanelResponse;
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


  public PostResponse getPostById(UUID id) {
    Optional<Post> postOptional = postRepository.findById(id);
    if (postOptional.isEmpty()) {
      throw new PostNotFoundException(id);
    }
    return toPostResponse(postOptional.get());
  }

  public Post getFullPostById(UUID id) {
    Optional<Post> postOptional = postRepository.findById(id);
    if (postOptional.isEmpty()) {
      throw new PostNotFoundException(id);
    }
    return postOptional.get();
  }

  public List<PostResponse> getPosts() {
    return postRepository.findAll().stream().map(this::toPostResponse).toList();
  }

  public List<PostPanelResponse> getPostsForPanel(String email, String scope){

    User user = userService.findByEmail(email);

    boolean admin = user.hasRole(UserRole.ADMIN);

    if (!admin) {
      return postRepository.findAllByAuthorId(user.getId())
              .stream()
              .map(this::toPostPanelResponse)
              .toList();
    }

    if ("MINE".equals(scope)) {
      return postRepository.findAllByAuthorId(user.getId())
              .stream()
              .map(this::toPostPanelResponse)
              .toList();
    }

    return postRepository.findAll()
            .stream()
            .map(this::toPostPanelResponse)
            .toList();
  }

  @Transactional
  public UUID createPost(CreatePostDTO createPostDTO, String userEmail, Boolean publishNow) {

    Title title = new Title(createPostDTO.getTitle());
    Content content = new Content(createPostDTO.getContent());
    Thumbnail thumbnail = new Thumbnail(createPostDTO.getThumbnail());
    UUID id = UUID.randomUUID();

    User author = userService.findByEmail(userEmail);

    Post post = new Post(id, title, thumbnail, content, author);
    postRepository.save(post);

    if (author.hasRole(UserRole.ADMIN)) {
      post.setReviewer(author);
      if (publishNow) {
        post.setStatus(PostStatus.PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());
      } else {
        post.setStatus(PostStatus.APPROVED);
      }
    }

    return post.getId();
  }

  @Transactional
  public void updatePost(UUID postId, UpdatePostDTO updatePostDTO, String userEmail) {
    Post post = getPostWithPermissionCheck(postId, userEmail);

    post.setTitle(new Title(updatePostDTO.getTitle()));
    post.setContent(new Content(updatePostDTO.getContent()));
    post.setThumbnail(new Thumbnail(updatePostDTO.getThumbnail()));

    postRepository.save(post);
  }

  @Transactional
  public void deletePost(UUID postId, String userEmail) {

    Post post = getPostWithPermissionCheck(postId, userEmail);

    postRepository.delete(post);
  }
  private Post getPostWithPermissionCheck(UUID postId, String userEmail) {

    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException(postId));

    User user = userService.findByEmail(userEmail);

    if (post.getAuthor() == null) {
      throw new IllegalStateException("Post has no author assigned");
    }

    boolean isAuthor = post.getAuthor().getId().equals(user.getId());
    boolean isAdmin = user.hasRole(UserRole.ADMIN);

    if (!isAuthor && !isAdmin) {
      throw new ForbiddenException("You are not allowed to perform this action");
    }


    return post;
  }

  private PostResponse toPostResponse(Post post) {
    return new PostResponse(
        post.getId().toString(),
        post.getTitle().getTitle(),
        post.getSlug().getSlug(),
        post.getThumbnail().getThumbnail(),
        post.getAuthor().getEmail(),
        post.getStatus(),
        post.getContent().getContent(),
        post.getCreatedAt().toString(),
        post.getUpdatedAt().toString());
  }

  private PostPanelResponse toPostPanelResponse(Post post) {
    return new PostPanelResponse(
            post.getId(),
            post.getTitle().getTitle(),
            post.getContent().getContent(),
            post.getSlug().getSlug(),
            post.getAuthor().getEmail(),
            post.getStatus(),
            post.getReviewComment(),
            post.getReviewer() != null ? post.getReviewer().getEmail() : null,
            post.getCreatedAt().toString());
  }
}
