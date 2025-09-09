package com.jwt.jwt.exception;

import lombok.Getter;

@Getter
public class ReachedLimitAttemptsException extends RuntimeException{
    private final String username;

    public ReachedLimitAttemptsException(String username){
        super("Account is temporarily locked.");
        this.username = username;
    }
}
