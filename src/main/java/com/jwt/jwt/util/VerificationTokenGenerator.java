package com.jwt.jwt.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VerificationTokenGenerator {
    public static String generateVerificationToken(){
        return UUID.randomUUID().toString().substring(0,4);
    }
}
