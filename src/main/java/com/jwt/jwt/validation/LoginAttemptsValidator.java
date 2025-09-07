package com.jwt.jwt.validation;

import com.jwt.jwt.dto.LoginRequest;
import com.jwt.jwt.entity.LoginAttempt;
import com.jwt.jwt.repository.LoginAttemptRepository;
import com.jwt.jwt.service.CustomDetailsService;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class LoginAttemptsValidator implements ConstraintValidator<ValidLoginAttempts, LoginRequest> {
    //Dependency injection of the Service layer to tracks the attempts
    @Autowired
    private CustomDetailsService customDetailsService;
    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Override
    public boolean isValid(LoginRequest request, ConstraintValidatorContext context) {
        Optional<LoginAttempt> attemptOpt = loginAttemptRepository.findByUsername(request.getUsername());

        if(attemptOpt.isEmpty()) return true;

        LoginAttempt attempt = attemptOpt.get();

        if(attempt.getLockedUntil() != null && LocalDateTime.now().isBefore(attempt.getLockedUntil())){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Account is temporary locked, Try again after"+
                    Duration.between(LocalDateTime.now(), attempt.getLockedUntil()).toMinutes()+" minutes")
                    .addPropertyNode("username")
                    .addConstraintViolation();
            return false;
        }
        return attempt.getAttempts() < 5;
    }
}
