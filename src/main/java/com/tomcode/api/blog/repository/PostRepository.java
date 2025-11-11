package com.tomcode.api.blog.repository;

import com.tomcode.api.blog.entity.post.Post;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository {
    Post save(Post entity);
    void delete(Post entity);
    Optional<Post> findById(UUID s);
    List<Post> findAll();
    boolean existsById(UUID s);
    void deleteById(UUID uuid);
}
