package com.jwt.jwt.controller;

import com.jwt.jwt.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VerificationController {
    private final VerificationService verificationService;

    @Operation(summary = "Verify Email token", description = "Activates accounts if token is valid and not expired")
    @ApiResponse(responseCode = "200",description = "Email verified successfully ")
    @ApiResponse(responseCode = "401", description = "Token invalid")
    @ApiResponse(responseCode = "406", description = "Token expired")
    @GetMapping("/verify")
    public ResponseEntity<String> verifyToken(@RequestParam String token){
        verificationService.verifyToken(token);
        log.info("Token verified successfully");
        return ResponseEntity.ok("Email verified successfully !");
    }

    @PostMapping("/renew")
    @Operation(summary = "Renew verification token", description = "Renew your verification token in case expiration")
    public ResponseEntity<String> renewToken(@RequestParam String username){
        verificationService.renewToken(username);
        return ResponseEntity.ok("New verification token has been sent to your email");
    }
}

