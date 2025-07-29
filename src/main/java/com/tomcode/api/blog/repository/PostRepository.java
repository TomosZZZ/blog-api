package com.tomcode.api.blog.repository;

import com.tomcode.api.blog.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,String> {
}
