package com.jwt.jwt.controller;

import com.jwt.jwt.dto.AuthResponse;
import com.jwt.jwt.dto.LoginRequest;
import com.jwt.jwt.dto.RefreshRequest;
import com.jwt.jwt.dto.RegisterRequest;
import com.jwt.jwt.entity.VerificationToken;
import com.jwt.jwt.enumeration.Role;
import com.jwt.jwt.repository.UserRepository;
import com.jwt.jwt.repository.VerificationTokenRepository;
import com.jwt.jwt.service.CustomDetailsService;
import com.jwt.jwt.service.EmailService;
import com.jwt.jwt.service.RefreshTokenService;
import com.jwt.jwt.service.VerificationService;
import com.jwt.jwt.util.JwtUtils;
import com.jwt.jwt.util.VerificationTokenGenerator;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import com.jwt.jwt.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final CustomDetailsService customDetailsService;
    private final VerificationService verificationService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request){
        log.info("New registration attempt : username={}, email={}", request.getUsername(), request.getEmail());
        if(userRepository.existsByUsernameOrEmail(request.getUsername(),request.getEmail())){
            log.warn("Username or Email already in use");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or email is already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.addRole(Role.valueOf(request.getRole()));
        userRepository.save(user);
        String verificationToken = VerificationTokenGenerator.generateVerificationToken();
        VerificationToken vt = new VerificationToken();
        vt.setToken(verificationToken);
        vt.setUser(user);
        vt.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        verificationTokenRepository.save(vt);
        emailService.sendVerificationEmail(user,verificationToken);

        log.info("New registration attempt succeeded");
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }

    @Operation(summary = "login and receive access/refresh token")
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request, WebRequest webRequest){
        webRequest.setAttribute("username",request.getUsername(),WebRequest.SCOPE_SESSION);
        log.info("New login attempt : username ={}",request.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        customDetailsService.clearExpiredLockedUntil();
        customDetailsService.validateLoginAttempt(request.getUsername());
        verificationService.isValidUser(request.getUsername());
        String accessToken = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);
        Claims claims = jwtUtils.extractAllClaims(accessToken);
        log.info("New login attempt succeeded");
        customDetailsService.resetAttempts(request.getUsername());
        AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .username(userDetails.getUsername())
                .email(claims.get("email", String.class))
                .userId(UUID.fromString(claims.getId()))
                .expiresAt(claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .issuedAt(claims.getIssuedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .expiresIn((claims.getExpiration().getTime() - claims.getIssuedAt().getTime())/ 1000)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body("User Logged in successfully!");
    }

    @Operation(summary = "Refresh access token using refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest refreshRequest){
        String refreshToken = refreshRequest.getRefreshToken();
        String username = jwtUtils.extractUsername(refreshToken);
        log.info("Refresh token attempt for user : {}",username);
        try{

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Username not found !"));

            if(jwtUtils.isTokenValid(refreshToken, userDetails) && refreshTokenService.isValid(refreshToken)){
                log.info("Refresh attempt succeeded, Issuing new token for user : {}",username);
                String newAccessToken = jwtUtils.generateToken(user);
                String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);

                refreshTokenService.createRefreshToken(user,newRefreshToken, LocalDateTime.now().plusDays(7));

                Claims claims = jwtUtils.extractAllClaims(newAccessToken);

                return ResponseEntity.ok(AuthResponse.builder()
                        .token(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .username(userDetails.getUsername())
                        .email(claims.get("email", String.class))
                        .userId(UUID.fromString(claims.getId()))
                        .expiresAt(claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .issuedAt(claims.getIssuedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .expiresIn((claims.getExpiration().getTime() - claims.getIssuedAt().getTime())/ 1000)
                        .build()
                );
            }else{
                log.warn("Refresh attempt failed : username = {}", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }catch (Exception e){
            log.warn("Refresh attempt failed : username = {}, cause = {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


}
