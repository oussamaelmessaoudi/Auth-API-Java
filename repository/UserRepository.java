package com.jwt.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jwt.jwt.entity.User;
import java.util.UUID;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User,UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
