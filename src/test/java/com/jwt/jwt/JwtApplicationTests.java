package com.jwt.jwt;

import com.jwt.jwt.repository.LoginAttemptRepository;
import com.jwt.jwt.repository.RefreshTokenRepository;
import com.jwt.jwt.repository.UserRepository;
import com.jwt.jwt.service.CustomDetailsService;
import com.jwt.jwt.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class JwtApplicationTests {
    @MockitoBean
    private LoginAttemptRepository loginAttemptRepository;
    @MockitoBean
    private RefreshTokenService refreshTokenService;
    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CustomDetailsService customDetailsService;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void contextLoads() {
    }

}
