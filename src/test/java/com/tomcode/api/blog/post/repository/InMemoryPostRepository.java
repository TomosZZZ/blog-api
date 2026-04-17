package com.tomcode.api.blog.post.repository;

import com.tomcode.api.blog.post.entity.Post;
import java.util.*;

public class InMemoryPostRepository implements PostRepository {
  private Map<UUID, Post> posts = new HashMap<>();

  @Override
  public Post save(Post entity) {
    posts.put(entity.getId(), entity);
    return entity;
  }

  @Override
  public void delete(Post entity) {
    posts.remove(entity.getId());
  }

  @Override
  public Optional<Post> findById(UUID s) {
    return Optional.ofNullable(posts.get(s));
  }

  @Override
  public List<Post> findAll() {
    return posts.values().stream().toList();
  }

  @Override
  public boolean existsById(UUID s) {
    return posts.containsKey(s);
  }

  @Override
  public void deleteById(UUID uuid) {
    posts.remove(uuid);
  }

  @Override
  public List<Post> findAllByAuthorId(UUID authorId) {
    return posts.values().stream().filter(p -> p.getAuthor().getId().equals(authorId)).toList();
  }
}
