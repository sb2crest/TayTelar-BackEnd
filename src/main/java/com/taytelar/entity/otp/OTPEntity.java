package com.taytelar.entity.otp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "otp_data")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OTPEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    private Long id;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "otp_code")
    private String otpCode;

    @Column(name = "otp_verified")
    private boolean otpVerified;

    @Column(name = "user_type")
    private String userType;
}
