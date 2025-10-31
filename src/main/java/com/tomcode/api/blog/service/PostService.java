package com.tomcode.api.blog.service;

import com.tomcode.api.blog.dto.PostDTO;
import com.tomcode.api.blog.entity.post.*;
import com.tomcode.api.blog.exception.PostNotFoundException;
import com.tomcode.api.blog.exception.ResourceNotFoundException;
import com.tomcode.api.blog.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        Post post = new Post(title,thumbnail,content ,id);
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

    public Optional<Post> getPostById(UUID id) {
        return postRepository.findById(id);
    }

    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    @Transactional
    public void updatePost(PostDTO postDTO, UUID postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        Title title = new Title(postDTO.getTitle());
        Content content = new Content(postDTO.getContent());
        Thumbnail thumbnail = new Thumbnail(postDTO.getThumbnail());

        post.setTitle(title);
        post.setContent(content);
        post.setThumbnail(thumbnail);

        postRepository.save(post);
    }
}
