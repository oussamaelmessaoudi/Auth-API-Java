package com.jwt.jwt.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import java.util.*;
import java.time.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String token;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String refreshToken;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String username;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID userId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime expiresAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime issuedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long expiresIn;
}
