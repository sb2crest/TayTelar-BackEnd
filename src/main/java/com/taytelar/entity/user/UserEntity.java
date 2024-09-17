package com.taytelar.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taytelar.entity.order.OrderEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_data")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

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

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private List<AddressEntity> addressEntityList;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private List<OrderEntity> orderEntities;
}
