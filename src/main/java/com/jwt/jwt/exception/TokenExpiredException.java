package com.jwt.jwt.exception;

import lombok.Getter;

@Getter
public class TokenExpiredException extends RuntimeException{
    public enum Reason {
        EXPIRED,
        NOT_EXPIRED
    }

    private final Reason reason;

    public TokenExpiredException(String message,Reason reason){
        super(message);
        this.reason = reason;
    }

}
