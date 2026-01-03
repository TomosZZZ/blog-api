package com.tomcode.api.blog.user.repository;

import com.tomcode.api.blog.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<User, UUID>{
    Optional<User> findByEmail(String email);
}

@RequiredArgsConstructor
@Repository
class JpaBasedUserRepository implements UserRepository{
    private final JpaUserRepository jpaUserRepository;


    @Override
    public User save(User entity) {
        return jpaUserRepository.save(entity);
    }

    @Override
    public void delete(User entity) {
        jpaUserRepository.delete(entity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaUserRepository.existsById(id);
    }
}

