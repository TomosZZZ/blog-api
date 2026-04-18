package com.tomcode.api.blog.post.repository;

import com.tomcode.api.blog.post.entity.Post;
import com.tomcode.api.blog.post.entity.PostStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository {
    Post save(Post entity);
    void delete(Post entity);
    Optional<Post> findById(UUID s);
    List<Post> findAll();
    List<Post> findAllByStatus(PostStatus status);
    boolean existsById(UUID s);
    void deleteById(UUID uuid);
    List<Post> findAllByAuthorId(UUID id);
}
