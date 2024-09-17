package com.taytelar.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String id;

    private String firstName;

    private String lastName;

    private String emailAddress;

    private String phoneNumber;

    private boolean phoneNumberVerified;

    private String referralCode;
}
