package com.tomcode.api.blog.user.repository;

import com.tomcode.api.blog.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository  {
        User save(User entity);
        void delete(User entity);
        Optional<User> findById(UUID id);
        List<User> findAll();
        Optional<User> findByEmail(String email);
        boolean existsById(UUID id);
}
