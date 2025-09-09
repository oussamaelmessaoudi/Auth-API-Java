package com.jwt.jwt.dto;

import com.jwt.jwt.validation.ValidLoginAttempts;
import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message="{login.username.blank}")
    @Size(min=5, max=12 ,message = "{login.username.size}")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,12}$",
    message = "{login.username.pattern}")
    private String username;
    @NotBlank(message = "{login.password.blank}")
    private String password;

}
