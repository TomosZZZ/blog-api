package com.tomcode.api.blog.service;

import com.tomcode.api.blog.entity.user.User;
import com.tomcode.api.blog.entity.user.UserRole;
import com.tomcode.api.blog.exception.ForbiddenException;
import com.tomcode.api.blog.exception.UserNotFoundException;
import com.tomcode.api.blog.repository.UserRepository;
import io.jsonwebtoken.Claims;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional
  public void deleteUser(String userIdToDelete, Claims claims) {
    String userFromSessionId = claims.get("sub", String.class);

    UserRole roleFromToken = claims.get("role", UserRole.class);

    if (UserRole.ADMIN != roleFromToken && userFromSessionId.equals(userIdToDelete)) {
      throw new ForbiddenException("You cannot delete your own admin account");
    }

    if (!userRepository.existsById(userFromSessionId)) {
      throw new UserNotFoundException(userFromSessionId);
    }
    userRepository.deleteById(userFromSessionId);
  }

  @Transactional
  public void updateUserRole(String userIdToUpdate, Claims claims, String roleToUpdate) {
    String roleFromTokenString = claims.get("role", String.class);
    UserRole roleFromToken = UserRole.valueOf(roleFromTokenString);
    String userFromSessionId = claims.get("sub", String.class);
    System.out.println(roleToUpdate);
    if (roleFromToken != UserRole.ADMIN) {
      throw new ForbiddenException("You do not have permission to change role");
    } else if (userFromSessionId.equals(userIdToUpdate)) {
      throw new ForbiddenException("You cannot change your own admin role");
    }

    User user =
        userRepository
            .findById(userIdToUpdate)
            .orElseThrow(() -> new UserNotFoundException(userIdToUpdate));

    UserRole newRole;
    try {
      newRole = UserRole.valueOf(roleToUpdate.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("There is no " + roleToUpdate + " role");
    }
    user.setRole(newRole);
  }
}
