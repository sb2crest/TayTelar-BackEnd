package com.taytelar.request.otp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTPRequest {

    @NotBlank(message = "Phone Number is mandatory")
    @Pattern(regexp = "\\d{10}", message = "Phone Number must be exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "User Type cannot be null or empty")
    @Pattern(regexp = "customer|affiliate", message = "User Type must be either 'customer' or 'affiliate'")
    private String userType;

    @NotBlank(message = "Request Type cannot be null or empty")
    @Pattern(regexp = "login|register", message = "Request Type must be either 'login' or 'register'")
    private String requestType;
}
