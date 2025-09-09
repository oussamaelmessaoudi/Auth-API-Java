package com.jwt.jwt.exception;

import com.jwt.jwt.entity.LoginAttempt;
import com.jwt.jwt.repository.LoginAttemptRepository;
import com.jwt.jwt.service.CustomDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.jwt.jwt.dto.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final CustomDetailsService customDetailsService;
    private final static int MAX_ATTEMPTS = 5;
    private final LoginAttemptRepository loginAttemptRepository;

    @ExceptionHandler(ReachedLimitAttemptsException.class)
    public ResponseEntity<ErrorResponse> handleReachedLimitAttempts(ReachedLimitAttemptsException ex){
        String username = ex.getUsername();
        LoginAttempt loginAttempt = loginAttemptRepository.findByUsername(username)
                .orElse(LoginAttempt.builder()
                        .username(username)
                        .attempts(0)
                        .lastAttempt(LocalDateTime.now())
                        .lockedUntil(null).build());
        long duration = loginAttempt.getLockedUntil() != null ? Duration.between(LocalDateTime.now(), loginAttempt.getLockedUntil()).toMinutes()
                : 0;
        String message = ex.getMessage() +" Please try again after "+ duration +" minutes.";
        return buildError(message,HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, WebRequest request){
        String username = extractUsernameFromRequest(request);
        customDetailsService.incrementAttempts(username);
        if(customDetailsService.getLoginAttempts(username) >= MAX_ATTEMPTS){
            throw new ReachedLimitAttemptsException(username);
        }
        return buildError("Invalid Username or Password, "
                +(MAX_ATTEMPTS - customDetailsService.getLoginAttempts(username))+" Login attempts remaining",HttpStatus.UNAUTHORIZED);
    }

    //Helper method to extract username from request
    private String extractUsernameFromRequest(WebRequest request){
        Object username = request.getAttribute("username",WebRequest.SCOPE_SESSION);
        return username == null ? null :  username.toString();
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex){
        return buildError("User not found",HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGenerics(Exception ex){
//        return buildError("Unexpected Error",HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    public ResponseEntity<ErrorResponse> buildError(String message, HttpStatus status){
        return ResponseEntity.status(status).body(new ErrorResponse(message, status.value(), LocalDateTime.now()));
    }
}
