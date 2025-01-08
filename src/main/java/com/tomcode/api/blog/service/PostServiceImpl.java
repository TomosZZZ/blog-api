package com.tomcode.api.blog.service;

import com.tomcode.api.blog.entity.Post;
import com.tomcode.api.blog.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public void createPost(Post post) {
         postRepository.save(post);
    }

    @Override
    public List<Post> getPosts() {
        return List.of();
    }
}
