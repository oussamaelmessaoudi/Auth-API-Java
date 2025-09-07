package com.jwt.jwt.repository;

import com.jwt.jwt.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, UUID> {
    Optional<LoginAttempt> findByUsername(String username);
}
