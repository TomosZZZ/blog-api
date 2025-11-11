package com.tomcode.api.blog.service;

import com.tomcode.api.blog.dto.PostDTO;
import com.tomcode.api.blog.dto.PostResponse;
import com.tomcode.api.blog.entity.post.*;
import com.tomcode.api.blog.exception.PostNotFoundException;
import com.tomcode.api.blog.repository.PostRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

  private PostRepository postRepository;

  public PostService(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Transactional
  public UUID createPost(PostDTO postDTO) {

    Title title = new Title(postDTO.getTitle());
    Content content = new Content(postDTO.getContent());
    Thumbnail thumbnail = new Thumbnail(postDTO.getThumbnail());

    UUID id = UUID.randomUUID();
    Post post = new Post(title, thumbnail, content, id);
    postRepository.save(post);
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
  public void updatePost(PostDTO postDTO, UUID postId) {

    Post post =
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

    Title title = new Title(postDTO.getTitle());
    Content content = new Content(postDTO.getContent());
    Thumbnail thumbnail = new Thumbnail(postDTO.getThumbnail());

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
