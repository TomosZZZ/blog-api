package com.tomcode.api.blog.user.service;

import com.tomcode.api.blog.common.exception.BadRequestException;
import com.tomcode.api.blog.common.exception.EmailAlreadyExistsException;
import com.tomcode.api.blog.common.exception.ForbiddenException;
import com.tomcode.api.blog.common.exception.UserNotFoundException;
import com.tomcode.api.blog.user.dto.CreateUserDTO;
import com.tomcode.api.blog.user.entity.User;
import com.tomcode.api.blog.user.entity.UserRole;
import com.tomcode.api.blog.user.repository.InMemoryUserRepository;
import com.tomcode.api.blog.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;

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


    @BeforeEach
    public void setUp() {
        userRepository = new InMemoryUserRepository();
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void shouldCreateUser() {
        // given
        CreateUserDTO dto =
                new CreateUserDTO("test@mail.com", "password", "user");

        // when
        UUID userId = userService.createUser(dto);
        User user = userRepository.findById(userId).orElseThrow();

        // then
        assertAll(
                () -> assertEquals("test@mail.com", user.getEmail()),
                () -> assertEquals("user", user.getUsername()),
                () -> assertTrue(passwordEncoder.matches("password", user.getPassword()))
        );
    }
    @Test
    public void createUser_shouldThrowEmailAlreadyExistsException() {
        CreateUserDTO createUserDTO = new CreateUserDTO("test@mail.com", "password", "user");
        UUID userId = userService.createUser(createUserDTO);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(createUserDTO));
    }

    @Test
    public void shouldDeleteUser() {
        User admin = createAdmin("admin@mail.com");
        User user = createUser("user@mail.com");

        userService.deleteUser(user.getId(), admin.getEmail());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(user.getId()));
    }

    @Test
    public void deleteUser_shouldThrowUserNotFoundExceptionWhenAdminNotFound(){
        User user = createUser("user@mail.com");

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(user.getId(), "fakeEmail@mail.com"));
    }

    @Test
    public void deleteUser_shouldThrowUserNotFoundExceptionWhenUserNotFound(){
        User admin = createAdmin("admin@mail.com");

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(UUID.randomUUID(), admin.getEmail()));
    }

    @Test
    public void deleteUser_shouldThrowForbiddenExceptionWhenAdminDeletesHimself(){
        User admin = createAdmin("admin@mail.com");
        assertThrows(ForbiddenException.class, () -> userService.deleteUser(admin.getId(), admin.getEmail()));
    }

    @Test
    public void shouldUpdateUserRole(){
        User admin = createAdmin("admin@mail.com");
        User user = createUser("user@mail.com");

        userService.updateUserRole(user.getId(), "EDITOR", admin.getEmail());

        assertEquals("EDITOR", user.getRole().toString());
    }

    @Test
    public void updateUserRole_shouldThrowUserNotFoundExceptionWhenUserNotFound(){
        User admin = createAdmin("admin@mail.com");

        assertThrows(UserNotFoundException.class, () ->  userService.updateUserRole(UUID.randomUUID(), "EDITOR", admin.getEmail()));
    }

    @Test
    public void updateUserRole_shouldThrowUserNotFoundExceptionWhenAdminNotFound(){
        User user = createUser("user@mail.com");

        assertThrows(UserNotFoundException.class, () ->  userService.updateUserRole(user.getId(), "EDITOR", "fakeEmail@mail.com"));
    }

    @Test
    public void updateUserRole_shouldThrowForbiddenExceptionWhenAdminUpdatesHisRole(){
        User admin = createAdmin("admin@mail.com");

        assertThrows(ForbiddenException.class, () ->  userService.updateUserRole(admin.getId(), "EDITOR", admin.getEmail()));
    }

    @Test
    public void updateUserRole_shouldThrowBadRequestExceptionWhenInvalidRole(){
        User admin = createAdmin("admin@mail.com");
        User user = createUser("user@mail.com");

        assertThrows(BadRequestException.class, () ->  userService.updateUserRole(user.getId(), "NOT_SUPPORTED_ROLE", admin.getEmail()));
    }

    @Test
    void updateUserRole_shouldNotChangeAnythingWhenExceptionThrown() {
        User admin = createAdmin("admin@mail.com");
        User user = createUser("user@mail.com");

        assertThrows(BadRequestException.class, () ->
                userService.updateUserRole(user.getId(), "INVALID", admin.getEmail())
        );

        assertEquals(UserRole.USER, user.getRole());
    }



}
