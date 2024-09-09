package com.taytelar.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Number or Email is mandatory")
    private String numberOrEmail;

    @NotBlank(message = "Password is mandatory")
    private String password;
}
