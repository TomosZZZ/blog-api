package com.tomcode.api.blog.user.service;

import com.tomcode.api.blog.common.exception.BadRequestException;
import com.tomcode.api.blog.common.exception.EmailAlreadyExistsException;
import com.tomcode.api.blog.common.exception.ForbiddenException;
import com.tomcode.api.blog.common.exception.UserNotFoundException;
import com.tomcode.api.blog.user.dto.CreateUserDTO;
import com.tomcode.api.blog.user.entity.User;
import com.tomcode.api.blog.user.entity.UserRole;
import com.tomcode.api.blog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder;


    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    public User getUserById(UUID id) {
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(id.toString());
        }
        return userOptional.get();
    }

    @Transactional
    public UUID createUser(CreateUserDTO userDTO) {

    if (userRepo.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyExistsException(userDTO.getEmail());
        }
        String hashedPassword = encoder.encode(userDTO.getPassword());
        User user = new User(userDTO.getEmail(), hashedPassword, userDTO.getUsername());
        return userRepo.save(user).getId();
    }

    @Transactional
    public void deleteUser(UUID userId, String adminEmail) {

        User admin = userRepo.findByEmail(adminEmail)
                .orElseThrow(() -> new UserNotFoundException(adminEmail));

        User userToDelete = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        if (admin.getId().equals(userToDelete.getId())) {
      throw new ForbiddenException("You cannot delete yourself");
        }

        userRepo.delete(userToDelete);
    }

    @Transactional
    public void updateUserRole(
            UUID userId,
            String role,
            String adminEmail) {

        User admin = userRepo.findByEmail(adminEmail)
                .orElseThrow(() -> new UserNotFoundException(adminEmail));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        if (admin.getId().equals(user.getId())) {
      throw new ForbiddenException("You cannot change your own role");
        }

        UserRole newRole;
        try {
            newRole = UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + role);
        }

        user.setRole(newRole);
        userRepo.save(user);
    }

}
