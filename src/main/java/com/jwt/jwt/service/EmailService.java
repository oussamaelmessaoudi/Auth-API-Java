package com.jwt.jwt.service;

import com.jwt.jwt.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(User user, String token){
        String link = "http://localhost:8081/api/verify?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Verification Email");
        message.setText("Click the link to verify your account : "+link);
        mailSender.send(message);
    }

}
