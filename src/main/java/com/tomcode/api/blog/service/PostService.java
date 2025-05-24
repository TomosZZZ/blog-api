package com.tomcode.api.blog.service;

import com.tomcode.api.blog.dto.PostDTO;
import com.tomcode.api.blog.entity.Post;

import java.util.List;

public interface PostService {
    public void createPost(PostDTO post);
    public List<Post> getPosts();

}
