package com.example.smart_task_manager.repository;

import com.example.smart_task_manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // To find user by email (for login, validation, etc.)
    Optional<User> findByEmail(String email);
}

