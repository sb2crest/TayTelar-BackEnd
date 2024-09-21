package com.taytelar.entity.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_data")
@Getter
@Setter
public class AdminEntity {

    @Id
    @Column(name = "admin_id", nullable = false)
    private String adminId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "email_address_verified")
    private boolean emailAddressVerified;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "phone_number_verified")
    private boolean phoneNumberVerified;

    @Column(name = "referralCode")
    private String referralCode;

    @Column(name = "referred_referral_code")
    private String referredReferralCode;

    @Column(name = "authentication_source")
    private String authenticationSource;

    @Column(name = "user_created_at")
    private LocalDateTime userCreatedAt;
}