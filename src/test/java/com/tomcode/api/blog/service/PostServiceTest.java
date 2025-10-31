package com.tomcode.api.blog.service;

import com.tomcode.api.blog.dto.PostDTO;
import com.tomcode.api.blog.entity.post.Post;
import com.tomcode.api.blog.exception.PostNotFoundException;
import com.tomcode.api.blog.repository.InMemoryPostRepository;
import com.tomcode.api.blog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.util.Optional;
import java.util.UUID;

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
        assertTrue(postService.getPostById(id).isEmpty(), "Post should be removed");
        assertFalse(postRepository.existsById(id), "Repository should report non-existence");
    }

    @Test
    public void shouldCreatePost(){
        // given
        PostDTO dto = new PostDTO("Post", "Lorem ipsum", "aGVsbG8=");

        // when
        UUID id = postService.createPost(dto);

        // then
        Optional<Post> maybePost = postService.getPostById(id);
        assertTrue(maybePost.isPresent(), "Post should be present after creation");
        Post p = maybePost.get();

        assertEquals(id, p.getId(), "Returned id should match entity id");
        assertEquals(dto.getTitle(), p.getTitle().getTitle());
        assertEquals(dto.getContent(), p.getContent().getContent());
        assertEquals(dto.getThumbnail(), p.getThumbnail().getThumbnail());

    }

    @Test
    void shouldUpdatePost_fieldsAreChanged() {
        // given
        UUID id = postService.createPost(new PostDTO("Old", "Old content", "aGVsbG8="));

        // when
        PostDTO updated = new PostDTO("New", "New content", "bmV3");
        postService.updatePost(updated, id);

        // then
        Post p = postService.getPostById(id).orElseThrow();
        assertAll(
                () -> assertEquals("New", p.getTitle().getTitle()),
                () -> assertEquals("New content", p.getContent().getContent()),
                () -> assertEquals("bmV3", p.getThumbnail().getThumbnail())
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
