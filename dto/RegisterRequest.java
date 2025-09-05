package com.jwt.jwt.dto;
import com.jwt.jwt.validation.ValidRole;
import jakarta.validation.constraints.*;
import lombok.*;


@Builder
@Getter
@AllArgsConstructor
public final class RegisterRequest {
    @NotBlank(message="{register.username.blank}")
    @Size(min=5, max=12, message = "{register.username.size}")
    @Pattern(regexp="^[a-zA-Z0-9]{5,12}$",
            message = "{register.username.pattern}")
    private final String username;

    @NotBlank(message = "{register.email.blank}")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
            , message = "{register.email.pattern}")
    private final String email;

    @NotBlank(message="{register.password.blank}")
    @Size(min=5, max= 15, message="{register.password.size}")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{5,15}$",
            message = "{register.password.pattern}")
    private final String password;

    @NotBlank(message="{register.role.pattern}")
    // we can use Role enum which is good for type-safety and auto-validated, but i tried to create a custom validation annotation
    @ValidRole(message = "{register.role.invalid}")
    private final String role;
}
