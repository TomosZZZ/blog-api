package com.tomcode.api.blog.user.repository;

import com.tomcode.api.blog.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {}
