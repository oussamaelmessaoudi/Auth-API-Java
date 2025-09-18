package com.jwt.jwt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "verification_tokens")
@Getter
@Setter
@Builder
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false,unique = true)
    private long tokenId;

    @Column(nullable = false,unique = true)
    private String token;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false, name = "expiry_date")
    private LocalDateTime expiryDate;

    public boolean isExpired(){
        return expiryDate.isBefore(LocalDateTime.now());
    }
}
