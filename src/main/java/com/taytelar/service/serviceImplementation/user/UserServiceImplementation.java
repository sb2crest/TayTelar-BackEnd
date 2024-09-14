package com.taytelar.service.serviceImplementation.user;

import com.taytelar.entity.affiliate.AffiliateUserEntity;
import com.taytelar.entity.otp.OTPEntity;
import com.taytelar.entity.user.UserEntity;
import com.taytelar.exception.otp.OtpNotFoundException;
import com.taytelar.exception.user.UserDetailsMissMatchException;
import com.taytelar.repository.AffiliateUserRepository;
import com.taytelar.repository.OTPRepository;
import com.taytelar.repository.UserRepository;
import com.taytelar.request.user.UserRequest;
import com.taytelar.response.user.RegisterResponse;
import com.taytelar.service.service.user.UserService;
import com.taytelar.util.Constants;
import com.taytelar.util.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    private final AffiliateUserRepository affiliateUserRepository;

    private final OTPRepository otpRepository;

    private final Generator generator;

    @Override
    public RegisterResponse register(UserRequest userRequest) {
        String referredReferralCode = null;

        if (userRequest.getReferralCode() != null && !userRequest.getReferralCode().isEmpty()) {
            referredReferralCode = checkReferralCode(userRequest.getReferralCode());
            if (referredReferralCode == null) {
                throw new UserDetailsMissMatchException(Constants.INVALID_REFERRAL_CODE);
            }
        }

        OTPEntity otpEntity = otpRepository.findByPhoneNumber(userRequest.getPhoneNumber());
        if (otpEntity == null) {
            throw new OtpNotFoundException(Constants.OTP_ENTITY_NOT_FOUND);
        }

        if (userRequest.getUserType().equalsIgnoreCase(Constants.CUSTOMER)) {
            if (otpEntity.isOtpVerified() && otpEntity.getPhoneNumber().equals(userRequest.getPhoneNumber())) {
                UserEntity user = new UserEntity();
                user.setUserId(generator.generateId(Constants.USER_ID));
                user.setFirstName(userRequest.getFirstName());
                user.setLastName(userRequest.getLastName());
                user.setUserType(userRequest.getUserType());
                user.setEmailAddress(userRequest.getEmailAddress());
                user.setPhoneNumber(userRequest.getPhoneNumber());
                user.setReferralCode(generator.referralCode());
                user.setReferredReferralCode(referredReferralCode);
                userRepository.save(user);

                RegisterResponse registerResponse = new RegisterResponse();
                registerResponse.setId(user.getUserId());
                registerResponse.setMessage(Constants.REGISTER_SUCCESS);
                return registerResponse;
            } else {
                throw new UserDetailsMissMatchException(Constants.USER_DETAILS_MISS_MATCH + Constants.OTP_NOT_VERIFIED);
            }
        } else {
            if (otpEntity.isOtpVerified() && otpEntity.getPhoneNumber().equals(userRequest.getPhoneNumber())) {
                AffiliateUserEntity affiliateUserEntity = new AffiliateUserEntity();
                affiliateUserEntity.setAffiliateUserId(generator.generateId(Constants.AFFILIATE_USER_ID));
                affiliateUserEntity.setFirstName(userRequest.getFirstName());
                affiliateUserEntity.setLastName(userRequest.getLastName());
                affiliateUserEntity.setEmailAddress(userRequest.getEmailAddress());
                affiliateUserEntity.setPhoneNumber(userRequest.getPhoneNumber());
                affiliateUserEntity.setReferralCode(generator.referralCode());
                affiliateUserEntity.setReferredReferralCode(referredReferralCode);
                affiliateUserRepository.save(affiliateUserEntity);

                RegisterResponse registerResponse = new RegisterResponse();
                registerResponse.setId(affiliateUserEntity.getAffiliateUserId());
                registerResponse.setMessage(Constants.REGISTER_SUCCESS);
                return registerResponse;
            } else {
                throw new UserDetailsMissMatchException(Constants.USER_DETAILS_MISS_MATCH + Constants.OTP_NOT_VERIFIED);
            }
        }
    }

    private String checkReferralCode(String referralCode) {
        UserEntity userEntity = userRepository.findByReferralCode(referralCode);
        if (userEntity != null) {
            return userEntity.getReferralCode();
        } else {
            AffiliateUserEntity affiliateUser = affiliateUserRepository.findByReferralCode(referralCode);
            if (affiliateUser != null) {
                return affiliateUser.getReferralCode();
            } else {
                return null;
            }
        }
    }
}
