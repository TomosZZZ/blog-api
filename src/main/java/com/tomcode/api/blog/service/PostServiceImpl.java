package com.tomcode.api.blog.service;

import com.tomcode.api.blog.dto.PostDTO;
import com.tomcode.api.blog.entity.Post;
import com.tomcode.api.blog.exception.DatabaseOperationException;
import com.tomcode.api.blog.repository.PostRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void createPost(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setThumbnail(postDTO.getThumbnail());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        try{
            postRepository.save(post);
        }catch(Exception e){
            throw new DatabaseOperationException(e.getMessage());
        }

    }

    public Post getPostById(String id) {
        Optional<Post> post = postRepository.findById(id);
        return post.orElse(null);
    }

    @Override
    public List<Post> getPosts() {
        return postRepository.findAll();
    }
}
