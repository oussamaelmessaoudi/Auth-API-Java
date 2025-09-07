package com.jwt.jwt.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LoginAttemptsValidator.class)
public @interface ValidLoginAttempts {
    String message() default "{login.attempt.limit}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
