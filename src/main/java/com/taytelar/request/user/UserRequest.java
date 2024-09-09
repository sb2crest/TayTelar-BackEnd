package com.taytelar.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "First name is mandatory")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Email address is mandatory")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(com|net|org|in|co\\.uk|edu|gov|io)$",
            message = "Email should be valid and match allowed domains")
    private String emailAddress;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Password is mandatory")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!_-])[A-Za-z\\d@#$%^&+=!_-]{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;
}
