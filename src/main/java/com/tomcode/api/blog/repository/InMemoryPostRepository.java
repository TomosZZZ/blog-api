package com.tomcode.api.blog.repository;

import com.tomcode.api.blog.entity.post.Post;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

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
}
