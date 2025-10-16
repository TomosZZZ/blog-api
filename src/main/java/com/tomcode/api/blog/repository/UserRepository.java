package com.tomcode.api.blog.repository;

import com.tomcode.api.blog.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
