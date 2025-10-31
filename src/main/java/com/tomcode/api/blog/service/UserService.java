package com.tomcode.api.blog.service;

import com.tomcode.api.blog.entity.user.User;
import com.tomcode.api.blog.exception.ForbiddenException;
import com.tomcode.api.blog.exception.UserNotFoundException;
import com.tomcode.api.blog.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
public class UserService {

    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(String userIdToDelete, Claims claims) {
        String role = claims.get("role", String.class);
        String userFromSessionId = claims.get("sub", String.class);

        if("ADMIN".equals(role) && userFromSessionId.equals(userIdToDelete)){
            throw new ForbiddenException("You cannot delete your own admin account");
        }

        if (!userRepository.existsById(userFromSessionId)) {
            throw new UserNotFoundException(userFromSessionId);
        }
        userRepository.deleteById(userFromSessionId);
    }

}
