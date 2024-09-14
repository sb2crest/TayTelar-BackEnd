package com.taytelar.service.serviceImplementation.user;

import com.taytelar.entity.affiliate.AffiliateUserEntity;
import com.taytelar.entity.otp.OTPEntity;
import com.taytelar.entity.user.UserEntity;
import com.taytelar.exception.otp.OtpNotFoundException;
import com.taytelar.exception.otp.UnknownUserTypeException;
import com.taytelar.exception.user.UserAccountNotExistException;
import com.taytelar.exception.user.UserDetailsMissMatchException;
import com.taytelar.repository.AffiliateUserRepository;
import com.taytelar.repository.OTPRepository;
import com.taytelar.repository.UserRepository;
import com.taytelar.request.user.LoginRequest;
import com.taytelar.request.user.UserRequest;
import com.taytelar.response.user.LoginResponse;
import com.taytelar.response.user.RegisterResponse;
import com.taytelar.service.service.user.UserService;
import com.taytelar.util.Constants;
import com.taytelar.util.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
            UserEntity userEntity = userRepository.findUserByPhoneNumber(userRequest.getPhoneNumber());
            if (userEntity ==  null && otpEntity.isOtpVerified() && otpEntity.getPhoneNumber().equals(userRequest.getPhoneNumber())) {
                UserEntity user = new UserEntity();
                user.setUserId(generator.generateId(Constants.USER_ID));
                user.setFirstName(userRequest.getFirstName());
                user.setLastName(userRequest.getLastName());
                user.setUserType(userRequest.getUserType());
                user.setEmailAddress(userRequest.getEmailAddress());
                user.setPhoneNumber(userRequest.getPhoneNumber());
                user.setPhoneNumberVerified(otpEntity.isOtpVerified());
                user.setReferralCode(generator.referralCode());
                user.setReferredReferralCode(referredReferralCode);
                userRepository.save(user);

                RegisterResponse registerResponse = new RegisterResponse();
                registerResponse.setId(user.getUserId());
                registerResponse.setPhoneNumber(user.getPhoneNumber());
                registerResponse.setMessage(Constants.REGISTER_SUCCESS);
                registerResponse.setStatusCode(HttpStatus.OK.value());
                return registerResponse;
            } else {
                throw new UserDetailsMissMatchException(Constants.USER_DETAILS_MISS_MATCH + Constants.OTP_NOT_VERIFIED);
            }
        } else {
            AffiliateUserEntity affiliateUser = affiliateUserRepository.findUserByPhoneNumber(userRequest.getPhoneNumber());
            if (affiliateUser == null && otpEntity.isOtpVerified() && otpEntity.getPhoneNumber().equals(userRequest.getPhoneNumber())) {
                AffiliateUserEntity affiliateUserEntity = new AffiliateUserEntity();
                affiliateUserEntity.setAffiliateUserId(generator.generateId(Constants.AFFILIATE_USER_ID));
                affiliateUserEntity.setFirstName(userRequest.getFirstName());
                affiliateUserEntity.setLastName(userRequest.getLastName());
                affiliateUserEntity.setEmailAddress(userRequest.getEmailAddress());
                affiliateUserEntity.setPhoneNumber(userRequest.getPhoneNumber());
                affiliateUserEntity.setPhoneNumberVerified(otpEntity.isOtpVerified());
                affiliateUserEntity.setReferralCode(generator.referralCode());
                affiliateUserEntity.setReferredReferralCode(referredReferralCode);
                affiliateUserRepository.save(affiliateUserEntity);

                RegisterResponse registerResponse = new RegisterResponse();
                registerResponse.setId(affiliateUserEntity.getAffiliateUserId());
                registerResponse.setPhoneNumber(affiliateUserEntity.getPhoneNumber());
                registerResponse.setMessage(Constants.REGISTER_SUCCESS);
                registerResponse.setStatusCode(HttpStatus.OK.value());
                return registerResponse;
            } else {
                throw new UserDetailsMissMatchException(Constants.USER_DETAILS_MISS_MATCH + Constants.OTP_NOT_VERIFIED);
            }
        }
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse loginResponse = new LoginResponse();
        if (loginRequest.getRequestType().equalsIgnoreCase(Constants.LOGIN) && loginRequest.getUserType().equalsIgnoreCase(Constants.CUSTOMER)) {
            UserEntity userEntity = userRepository.findUserByPhoneNumber(loginRequest.getPhoneNumber());
            if (userEntity != null && userEntity.isPhoneNumberVerified()) {
                loginResponse.setId(userEntity.getUserId());
                loginResponse.setFirstName(userEntity.getFirstName());
                loginResponse.setLastName(userEntity.getLastName());
                loginResponse.setEmailAddress(userEntity.getEmailAddress());
                loginResponse.setPhoneNumber(userEntity.getPhoneNumber());
                loginResponse.setReferralCode(userEntity.getReferralCode());
                loginResponse.setPhoneNumberVerified(userEntity.isPhoneNumberVerified());
            } else {
                throw new UserAccountNotExistException(Constants.USER_ACCOUNT_NOT_EXIST);
            }
        } else if (loginRequest.getRequestType().equalsIgnoreCase(Constants.LOGIN) && loginRequest.getUserType().equalsIgnoreCase(Constants.AFFILIATE)) {
            AffiliateUserEntity affiliateUser = affiliateUserRepository.findUserByPhoneNumber(loginRequest.getPhoneNumber());
            if (affiliateUser != null && affiliateUser.isPhoneNumberVerified()) {
                loginResponse.setId(affiliateUser.getAffiliateUserId());
                loginResponse.setFirstName(affiliateUser.getFirstName());
                loginResponse.setLastName(affiliateUser.getLastName());
                loginResponse.setEmailAddress(affiliateUser.getEmailAddress());
                loginResponse.setPhoneNumber(affiliateUser.getPhoneNumber());
                loginResponse.setReferralCode(affiliateUser.getReferralCode());
                loginResponse.setPhoneNumberVerified(affiliateUser.isPhoneNumberVerified());
            } else {
                throw new UserAccountNotExistException(Constants.AFFILIATE_USER_ACCOUNT_NOT_EXIST);
            }
        } else {
            throw new UnknownUserTypeException(Constants.UNKNOWN_USER_TYPE);
        }
        return loginResponse;
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
