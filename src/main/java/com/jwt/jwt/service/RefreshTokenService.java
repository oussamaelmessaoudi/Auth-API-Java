package com.jwt.jwt.service;

import com.jwt.jwt.entity.RefreshToken;
import com.jwt.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jwt.jwt.entity.User;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(User user, String token, LocalDateTime expiredAt){
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .issuedAt(LocalDateTime.now())
                .expiresAt(expiredAt)
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean isValid(String token){
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> !rt.isRevoked() && rt.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public void revokeRefreshToken(String token){
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

}
