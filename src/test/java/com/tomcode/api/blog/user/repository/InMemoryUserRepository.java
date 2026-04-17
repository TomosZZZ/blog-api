package com.tomcode.api.blog.user.repository;

import com.tomcode.api.blog.user.entity.User;

import java.util.*;

public class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return users.containsKey(id);
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values()
                .stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}

