package com.tomcode.api.blog.post.service;

import com.tomcode.api.blog.common.exception.ForbiddenException;
import com.tomcode.api.blog.common.exception.PostNotFoundException;
import com.tomcode.api.blog.common.exception.UserNotFoundException;
import com.tomcode.api.blog.post.dto.ChangeStatusDTO;
import com.tomcode.api.blog.post.dto.CreatePostDTO;
import com.tomcode.api.blog.post.entity.Post;
import com.tomcode.api.blog.post.entity.PostStatus;
import com.tomcode.api.blog.post.repository.InMemoryPostRepository;
import com.tomcode.api.blog.post.repository.PostRepository;
import com.tomcode.api.blog.user.dto.CreateUserDTO;
import com.tomcode.api.blog.user.entity.User;
import com.tomcode.api.blog.user.entity.UserRole;
import com.tomcode.api.blog.user.repository.InMemoryUserRepository;
import com.tomcode.api.blog.user.repository.UserRepository;
import com.tomcode.api.blog.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PostLifecycleServiceTests {
    private  PostService postService;
    private  PostLifecycleService postLifecycleService;
    private  UserService userService;

    @BeforeEach
    public void setUp() {
        UserRepository userRepo = new InMemoryUserRepository();
        PostRepository postRepo = new InMemoryPostRepository();

        userService = new UserService(userRepo, new BCryptPasswordEncoder(4));
        postService = new PostService(postRepo, userService);
        postLifecycleService = new PostLifecycleService(postRepo, userService);
    }

    private User createUser(String email) {
        UUID id = userService.createUser(
                new CreateUserDTO(email, "password", "user123")
        );
        return userService.getUserById(id);
    }

    private User createAdmin(String email) {
        User admin = createUser(email);
        admin.setRole(UserRole.ADMIN);
        return admin;
    }

    private Post createPostWithGivenStatus(String email, PostStatus status) {
        CreatePostDTO dto = new CreatePostDTO("title", "content", "aGVsbG8=");
        UUID postId = postService.createPost(dto,email,false);
        Post post = postService.getFullPostById(postId);
        post.setStatus(status);
        return post;
    }

    @Test
    public void shouldChangeStatusFromDraftToInReview_whenAuthor(){
        User author = createUser("user@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.DRAFT);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.IN_REVIEW, null);

        postLifecycleService.changeStatus(dto, author.getEmail(), post.getId());

        assertEquals(PostStatus.IN_REVIEW, post.getStatus());
    }

    @Test
    public void changeStatusFromDraftToInReview_shouldThrowForbiddenException_whenNotAuthor(){
        User author = createUser("user@mail.com");
        User admin = createAdmin("admin@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.DRAFT);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.IN_REVIEW, null);

        assertThrows(ForbiddenException.class , ()-> postLifecycleService.changeStatus(dto, admin.getEmail(), post.getId()));
    }

    @Test
    public void shouldChangeStatusFromInReviewToApproved_whenAdmin(){
        User admin = createAdmin("admin@mail.com");
        User author = createUser("user@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.IN_REVIEW);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.APPROVED, null);

        postLifecycleService.changeStatus(dto, admin.getEmail(), post.getId());

        assertEquals(PostStatus.APPROVED, post.getStatus());
    }

    @Test
    public void shouldChangeStatusFromInReviewToReqChanges_whenAdmin(){
        User admin = createAdmin("admin@mail.com");
        User author = createUser("user@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.IN_REVIEW);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.CHANGES_REQ, "comment");

        postLifecycleService.changeStatus(dto, admin.getEmail(), post.getId());

        assertEquals(PostStatus.CHANGES_REQ, post.getStatus());
    }

    @Test
    public void changeStatusFromInReviewToReqChanges_shouldThrowForbiddenException_whenCommentIsNull(){
        User admin = createAdmin("admin@mail.com");
        User author = createUser("user@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.IN_REVIEW);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.CHANGES_REQ, null);

        assertThrows(ForbiddenException.class, ()-> postLifecycleService.changeStatus(dto, admin.getEmail(), post.getId()));
    }
    @Test
    public void changeStatusFromInReviewToReqChanges_shouldThrowForbiddenException_whenAuthor(){
        User author = createUser("user@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.IN_REVIEW);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.CHANGES_REQ, null);

        assertThrows(ForbiddenException.class, ()-> postLifecycleService.changeStatus(dto, author.getEmail(), post.getId()));
    }

    @Test
    public void changeStatusFromInReviewToApproved_shouldThrowForbiddenException_whenAuthor(){
        User author = createUser("user@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.IN_REVIEW);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.APPROVED, null);

        assertThrows(ForbiddenException.class, ()-> postLifecycleService.changeStatus(dto, author.getEmail(), post.getId()));
    }
    @Test
    public void shouldChangeStatusFromReqChangesToDraft_whenAuthor(){
        User author = createUser("user@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.CHANGES_REQ);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.DRAFT, null);

        postLifecycleService.changeStatus(dto, author.getEmail(), post.getId());

        assertEquals(PostStatus.DRAFT, post.getStatus());
    }

    @Test
    public void shouldChangeStatusFromReqChangesToInReview_whenAuthor(){
        User author = createUser("user@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.CHANGES_REQ);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.IN_REVIEW, null);

        postLifecycleService.changeStatus(dto, author.getEmail(), post.getId());

        assertEquals(PostStatus.IN_REVIEW, post.getStatus());
    }

    @Test
    public void changeStatusFromReqChangesToDraft_shouldThrowForbiddenException_whenAdmin(){
        User admin = createAdmin("admin@mail.com");
        User author = createUser("user@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.CHANGES_REQ);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.DRAFT, null);
        assertThrows(ForbiddenException.class, ()->    postLifecycleService.changeStatus(dto, admin.getEmail(), post.getId()));

    }

    @Test
    public void changeStatusFromReqChangesToInReview_shouldThrowForbiddenException_whenAdmin(){
        User admin = createAdmin("admin@mail.com");
        User author = createUser("user@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.CHANGES_REQ);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.IN_REVIEW, null);
        assertThrows(ForbiddenException.class, ()->    postLifecycleService.changeStatus(dto, admin.getEmail(), post.getId()));

    }

    @Test
    public void shouldChangeStatusFromApprovedToPublished_whenAuthor(){
        User author = createUser("user@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.APPROVED);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.PUBLISHED, null);

        postLifecycleService.changeStatus(dto, author.getEmail(), post.getId());

        assertEquals(PostStatus.PUBLISHED, post.getStatus());
    }
    @Test
    public void shouldChangeStatusFromApprovedToPublished_whenAdmin(){
        User admin = createAdmin("admin@mail.com");
        User author = createUser("user@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.APPROVED);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.PUBLISHED, null);

        postLifecycleService.changeStatus(dto, admin.getEmail(), post.getId());

        assertEquals(PostStatus.PUBLISHED, post.getStatus());
    }

    @Test
    public void changeFromPublished_shouldThrowForbiddenException(){
        User admin = createAdmin("admin@mail.com");
        User author = createUser("user@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.PUBLISHED);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.IN_REVIEW, null);
        assertThrows(ForbiddenException.class, ()->  postLifecycleService.changeStatus(dto, admin.getEmail(), post.getId()));
    }

    @Test
    public void changeStatus_shouldThrowWhenPostNotFound(){
        User author = createUser("user@mail.com");
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.DRAFT, null);
        assertThrows(PostNotFoundException.class, ()->postLifecycleService.changeStatus(dto,author.getEmail(), UUID.randomUUID()));
    }

    @Test
    public void changeStatus_shouldThrowWhenUserNotFound(){
        User author = createUser("user@mail.com");
        Post post = createPostWithGivenStatus(author.getEmail(), PostStatus.DRAFT);
        ChangeStatusDTO dto = new ChangeStatusDTO(PostStatus.DRAFT, null);
        assertThrows(UserNotFoundException.class, ()->postLifecycleService.changeStatus(dto,"fakeEmail@mail.com", post.getId()));
    }
}

