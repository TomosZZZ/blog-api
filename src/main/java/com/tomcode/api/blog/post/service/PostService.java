package com.tomcode.api.blog.post.service;

import com.tomcode.api.blog.common.exception.ForbiddenException;
import com.tomcode.api.blog.common.exception.PostNotFoundException;
import com.tomcode.api.blog.common.exception.UserNotFoundException;
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
  public UUID createPost(CreatePostDTO createPostDTO, String userEmail, Boolean publishNow) {

    Title title = new Title(createPostDTO.getTitle());
    Content content = new Content(createPostDTO.getContent());
    Thumbnail thumbnail = new Thumbnail(createPostDTO.getThumbnail());
    UUID id = UUID.randomUUID();

    Optional<User> author = userService.findByEmail(userEmail);
    if(author.isEmpty()) {
      throw new UserNotFoundException(userEmail);
    }

    Post post = new Post(id, title, thumbnail, content, author.get());
    postRepository.save(post);

    if (author.get().hasRole(UserRole.ADMIN)) {
      post.setReviewer(author.get());
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

    User user = userService.findByEmail(userEmail)
            .orElseThrow(() -> new UserNotFoundException(userEmail));

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
