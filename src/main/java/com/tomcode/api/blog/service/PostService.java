package com.tomcode.api.blog.service;

import com.tomcode.api.blog.dto.PostDTO;
import com.tomcode.api.blog.entity.post.Post;
import com.tomcode.api.blog.entity.post.PostDetails;
import com.tomcode.api.blog.exception.PostNotFoundException;
import com.tomcode.api.blog.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    @Transactional
    public void createPost(PostDTO postDTO) {

        PostDetails details = new PostDetails(postDTO.getTitle(), postDTO.getContent(), postDTO.getThumbnail());
        Post post = new Post(details);
        postRepository.save(post);
        post.generateSlug();
    }

    @Transactional
    public void deletePost(String postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException(postId);
        }
        postRepository.deleteById(postId);
    }

    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }

    public List<Post> getPosts() {
        return postRepository.findAll();
    }
}
