package com.tomcode.api.blog.post.repository;

import com.tomcode.api.blog.post.entity.Post;
import com.tomcode.api.blog.post.entity.PostStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface JpaPostRepository extends JpaRepository<Post, UUID> {
  List<Post> findAllByAuthorId(UUID authorId);
  List<Post> findAllByStatus(PostStatus status);
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
  public Optional<Post> findById(UUID id) {
    return jpaPostRepository.findById(id);
  }

  @Override
  public List<Post> findAll() {
    return jpaPostRepository.findAll();
  }

  @Override
  public boolean existsById(UUID id) {
    return jpaPostRepository.existsById(id);
  }

  @Override
  public void deleteById(UUID uuid) {
    jpaPostRepository.deleteById(uuid);
  }

  @Override
  public List<Post> findAllByAuthorId(UUID id){
    return jpaPostRepository.findAllByAuthorId(id);
  }

  @Override
  public List<Post> findAllByStatus(PostStatus status) {
    return jpaPostRepository.findAllByStatus(status);
  }
}
