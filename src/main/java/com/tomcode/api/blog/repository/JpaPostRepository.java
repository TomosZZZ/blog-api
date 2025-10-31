package com.tomcode.api.blog.repository;

import com.tomcode.api.blog.entity.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaPostRepository extends JpaRepository<Post, UUID> {
}

@RequiredArgsConstructor
@Repository
class JpaBasedPostRepository implements PostRepository {
    private final JpaPostRepository jpaPostRepository;

    @Override
    public Post save(Post entity) {
        return jpaPostRepository.save(entity);
    }

    @Override
    public void delete(Post entity) {
        jpaPostRepository.delete(entity);
    }

    @Override
    public Optional<Post> findById(UUID s) {
        return jpaPostRepository.findById(s);
    }

    @Override
    public List<Post> findAll() {
        return jpaPostRepository.findAll();
    }

    @Override
    public boolean existsById(UUID s) {
        return jpaPostRepository.existsById(s);
    }

    @Override
    public void deleteById(UUID uuid) {
        jpaPostRepository.deleteById(uuid);
    }
}
