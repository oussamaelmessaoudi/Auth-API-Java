package com.jwt.jwt.validation;

import com.jwt.jwt.dto.LoginRequest;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.*;

public class LoginAttemptsValidator implements ConstraintValidator<ValidLoginAttempts, LoginRequest> {
    @Autowired
    //Dependency injection of the Service layer to tracks the attempts

    @Override
    public boolean isValid(LoginRequest request, ConstraintValidatorContext context) {
        return true;
    }
}
