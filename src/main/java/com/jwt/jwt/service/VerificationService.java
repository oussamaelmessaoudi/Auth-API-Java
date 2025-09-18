package com.jwt.jwt.service;

import com.jwt.jwt.entity.*;
import com.jwt.jwt.exception.*;
import com.jwt.jwt.repository.*;
import com.jwt.jwt.util.VerificationTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailSender;

    public void verifyToken(String token){
        if(token == null || token.isBlank()){
            throw new IllegalArgumentException("Token is null or blank");
        }
        VerificationToken vt = verificationTokenRepository.findByToken(token)
                    .orElseThrow(() -> new InvalidTokenException("Invalid Token"));

        if(vt.isExpired()){
            throw new TokenExpiredException("Token is expired",TokenExpiredException.Reason.EXPIRED);
        }

        User user = vt.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.deleteById(vt.getTokenId());
    }

    public void isValidUser(String username){
        User user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Username not found!"));
        if(!user.isEnabled())
            throw new UserNotVerifiedException("This user "+username+" isn't verified");
    }

    public void renewToken(String username){
        User user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Username not found!"));
        if(user.isEnabled()){
            throw new UserAlreadyVerifiedException("This user is already verified");
        }
        VerificationToken vt = verificationTokenRepository.findByUserId(user.getId()).orElseThrow(()-> new UsernameNotFoundException("User not found!"));
        if(!vt.isExpired()){
            throw new TokenExpiredException("Token isn't expired yet",TokenExpiredException.Reason.NOT_EXPIRED);
        }
        String newToken = VerificationTokenGenerator.generateVerificationToken();
        emailSender.sendVerificationEmail(user,newToken);
        vt.setToken(newToken);
        vt.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        verificationTokenRepository.save(vt);
    }
}
