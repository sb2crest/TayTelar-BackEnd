package com.taytelar.entity.affiliate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "affiliate_user_data")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AffiliateUserEntity {

    @Id
    @Field(name = "affiliate_user_id")
    private String affiliateUserId;

    @Field(name = "first_name")
    private String firstName;

    @Field(name = "last_name")
    private String lastName;

    @Field(name = "user_type")
    private String userType;

    @Field(name = "email_address")
    private String emailAddress;

    @Field(name = "phone_number")
    private String phoneNumber;

    @Field(name = "otp_password")
    private String otpPassword;

    @Field(name = "otp_verified")
    private boolean otpVerified;

    @Field(name = "referral_code")
    private String referralCode;

    @Field(name = "referred_referral_code")
    private String referredReferralCode;


    @Field(name = "source_entity")
    private List<SourceEntity> sourceEntities;

}
