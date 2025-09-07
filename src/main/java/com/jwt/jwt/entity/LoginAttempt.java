package com.jwt.jwt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="login_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private int attempts;

    @Column(name="last_attempt",nullable = false)
    private LocalDateTime lastAttempt;

    @Column(name="locked_until")
    private LocalDateTime lockedUntil;
}
