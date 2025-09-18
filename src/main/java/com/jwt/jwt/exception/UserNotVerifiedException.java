package com.jwt.jwt.exception;

public class UserNotVerifiedException extends RuntimeException{
    public UserNotVerifiedException(String message){
        super(message);
    }
}
