package com.tomcode.api.blog.service;

import static org.junit.jupiter.api.Assertions.*;

import com.tomcode.api.blog.dto.PostDTO;
import com.tomcode.api.blog.dto.PostResponse;
import com.tomcode.api.blog.exception.PostNotFoundException;
import com.tomcode.api.blog.repository.InMemoryPostRepository;
import com.tomcode.api.blog.repository.PostRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PostServiceTest {
    PostRepository postRepository;
    PostService postService;

    @BeforeEach
    void setUp() {
        postRepository = new InMemoryPostRepository();
        postService = new PostService(postRepository);
    }



    @Test
    public void shouldDeletePost(){
        // given
        PostDTO postDto = new PostDTO("Title", "Content","aGVsbG8=");
        UUID id = postService.createPost(postDto);

        // when
        postService.deletePost(id);

        // then
        assertThrows(PostNotFoundException.class, () -> postService.getPostById(id));
        assertFalse(postRepository.existsById(id), "Repository should report non-existence");
    }

    @Test
    public void shouldCreatePost(){
        // given
        PostDTO dto = new PostDTO("Post", "Lorem ipsum", "aGVsbG8=");

        // when
        UUID id = postService.createPost(dto);

        // then
        PostResponse response = postService.getPostById(id);

        assertEquals(id.toString(), response.id());
        assertEquals(dto.getTitle(), response.title());
        assertEquals(dto.getContent(), response.content());
        assertEquals(dto.getThumbnail(), response.thumbnail());

    }

    @Test
    void shouldUpdatePost_fieldsAreChanged() {
        // given
        UUID id = postService.createPost(new PostDTO("Old", "Old content", "aGVsbG8="));

        // when
        PostDTO updated = new PostDTO("New", "New content", "bmV3");
        postService.updatePost(updated, id);

        // then
        PostResponse response = postService.getPostById(id);
        assertAll(
                () -> assertEquals("New", response.title()),
                () -> assertEquals("New content", response.content()),
                () -> assertEquals("bmV3", response.thumbnail())
        );
    }

    @Test
    void updatePost_shouldThrow_whenNotFound() {
        UUID randomId = UUID.randomUUID();
        PostDTO dto = new PostDTO("T", "C", "Th");
        assertThrows(PostNotFoundException.class, () -> postService.updatePost(dto, randomId));
    }

    @Test
    void deletePost_shouldThrow_whenNotFound() {
        UUID randomId = UUID.randomUUID();
        assertThrows(PostNotFoundException.class, () -> postService.deletePost(randomId));
    }


    @Test
    void createPost_shouldThrow_whenThumbnailInvalidBase64() {
        PostDTO dto = new PostDTO("Title", "Content", "not@base64!!");
        assertThrows(IllegalArgumentException.class, () -> postService.createPost(dto));
    }


}
