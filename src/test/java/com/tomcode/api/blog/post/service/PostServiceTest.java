package com.tomcode.api.blog.post.service;

import com.tomcode.api.blog.common.exception.ForbiddenException;
import com.tomcode.api.blog.common.exception.PostNotFoundException;
import com.tomcode.api.blog.common.exception.UserNotFoundException;
import com.tomcode.api.blog.post.dto.CreatePostDTO;
import com.tomcode.api.blog.post.dto.PostResponse;
import com.tomcode.api.blog.post.dto.UpdatePostDTO;
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

import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest {

  private PostRepository postRepository;
  private UserRepository userRepository;
  private UserService userService;
  private PostService postService;

  @BeforeEach
  void setUp() {
    postRepository = new InMemoryPostRepository();
    userRepository = new InMemoryUserRepository();
    userService = new UserService(userRepository, new BCryptPasswordEncoder(4));
    postService = new PostService(postRepository, userService);
  }

    /* =========================
       Helpers
       ========================= */

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

  private UUID createPost(String title, String content, String thumbnail, String authorEmail) {
    return postService.createPost(
            new CreatePostDTO(title, content, thumbnail),
            authorEmail,
            false
    );
  }

    /* =========================
       Tests
       ========================= */

  @Test
  void shouldCreatePost() {
    User user = createUser("user@mail.com");

    UUID postId = createPost(
            "Post",
            "Lorem ipsum",
            "aGVsbG8=",
            user.getEmail()
    );

    PostResponse response = postService.getPostById(postId);

    assertAll(
            () -> assertEquals("Post", response.title()),
            () -> assertEquals("Lorem ipsum", response.content()),
            () -> assertEquals("aGVsbG8=", response.thumbnail())
    );
  }


  @Test
  void shouldDeletePost_whenAuthorDeletes() {
    User user = createUser("user@mail.com");
    UUID postId = createPost("Title", "Content", "aGVsbG8=", user.getEmail());


    postService.deletePost(postId, user.getEmail());

    assertThrows(PostNotFoundException.class,
            () -> postService.getPostById(postId));
  }
  @Test
  void shouldDeletePost_whenAdminDeletes() {
    User author = createUser("author@mail.com");
    User admin = createAdmin("admin@mail.com");
    

    UUID postId = createPost("Title", "Content", "aGVsbG8=", author.getEmail());

    postService.deletePost(postId, admin.getEmail());

    assertThrows(PostNotFoundException.class,
            () -> postService.getPostById(postId));
  }

  @Test
  void deletePost_shouldThrow_whenNotAuthorNorAdmin() {
    User author = createUser("author@mail.com");
    User otherUser = createUser("other@mail.com");

    UUID postId = createPost("Title", "Content", "aGVsbG8=", author.getEmail());

    assertThrows(ForbiddenException.class,
            () -> postService.deletePost(postId, otherUser.getEmail()));
  }
  @Test
  void deletePost_shouldThrow_whenPostNotFound() {
    User user = createUser("user@mail.com");

    assertThrows(PostNotFoundException.class,
            () -> postService.deletePost(UUID.randomUUID(), user.getEmail()));
  }
  @Test
  void deletePost_shouldThrow_whenUserNotFound() {
    User author = createUser("user@mail.com");
    UUID postId = createPost("Title", "Content","aGVsbG8=", author.getEmail());



    assertThrows(UserNotFoundException.class,
            () -> postService.deletePost(postId, "other@mail.com"));
  }

  @Test
  void shouldUpdatePost_whenAuthorUpdates() {
    User user = createUser("user@mail.com");
    UUID postId = createPost("Old", "Old content", "aGVsbG8=", user.getEmail());

    postService.updatePost(
            postId,
            new UpdatePostDTO("New", "New content", "bmV3"),
            user.getEmail()
    );

    PostResponse response = postService.getPostById(postId);

    assertAll(
            () -> assertEquals("New", response.title()),
            () -> assertEquals("New content", response.content()),
            () -> assertEquals("bmV3", response.thumbnail())
    );
  }

  @Test
  void shouldUpdatePost_whenAdminUpdates() {
    User author = createUser("author@mail.com");
    User admin = createAdmin("admin@mail.com");

    UUID postId = createPost("Old", "Old content", "aGVsbG8=", author.getEmail());

    postService.updatePost(
            postId,
            new UpdatePostDTO("Admin edit", "Admin content", "bmV3"),
            admin.getEmail()
    );

    PostResponse response = postService.getPostById(postId);

    assertEquals("Admin edit", response.title());
  }

  @Test
  void updatePost_shouldThrow_whenUserNotFound() {
    User user = createUser("user@mail.com");
    UUID postId = createPost("Title", "Content", "aGVsbG8=", user.getEmail());

    assertThrows(UserNotFoundException.class,
            () -> postService.updatePost(
                    postId,
                    new UpdatePostDTO("New", "New", "bmV3"),
                    "missing@mail.com"
            ));
  }

  @Test
  void updatePost_shouldThrow_whenPostNotFound() {
    User user = createUser("user@mail.com");

    assertThrows(PostNotFoundException.class,
            () -> postService.updatePost(
                    UUID.randomUUID(),
                    new UpdatePostDTO("New", "New", "bmV3"),
                    user.getEmail()
            ));
  }

  @Test
  void createPost_shouldThrow_whenThumbnailInvalidBase64() {
    User user = createUser("user@mail.com");

    CreatePostDTO dto =
            new CreatePostDTO("Title", "Content", "not@base64!!");

    assertThrows(IllegalArgumentException.class,
            () -> postService.createPost(dto, user.getEmail(), false));
  }

  @Test
  void getPosts_shouldReturnOnlyPublishedPosts() {
    User admin = createAdmin("admin@mail.com");
    createPost("Draft Post", "Content", "aGVsbG8=", admin.getEmail());
    postService.createPost(new CreatePostDTO("Published Post", "Content", "aGVsbG8="), admin.getEmail(), true);

    var posts = postService.getPosts();

    assertEquals(1, posts.size());
    assertEquals("Published Post", posts.get(0).title());
  }

}
