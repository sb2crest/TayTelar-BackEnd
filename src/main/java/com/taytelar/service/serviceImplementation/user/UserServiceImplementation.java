package com.taytelar.service.serviceImplementation.user;

import com.taytelar.entity.affiliate.AffiliateUserEntity;
import com.taytelar.entity.user.UserEntity;
import com.taytelar.exception.user.UserDetailsMissMatchException;
import com.taytelar.exception.user.UserNotFoundException;
import com.taytelar.repository.AffiliateUserRepository;
import com.taytelar.repository.UserRepository;
import com.taytelar.request.user.OTPRequest;
import com.taytelar.request.user.ValidateOTP;
import com.taytelar.request.user.UserRequest;
import com.taytelar.response.user.OTPResponse;
import com.taytelar.response.user.RegisterResponse;
import com.taytelar.service.service.user.UserService;
import com.taytelar.util.Constants;
import com.taytelar.util.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    private final AffiliateUserRepository affiliateUserRepository;

    private final Generator generator;
    private final static String VARIABLE_VALUES = "&variables_values=";
    private final static String ROUTE_NUMBERS = "&route=otp&numbers=";
    @Value("${api.key}")
    private String apiKey;
    @Value("${sms.url}")
    private String smsUrl;

    @Override
    public RegisterResponse register(UserRequest userRequest) {
        String referredReferralCode = null;

        if (userRequest.getReferralCode() != null && !userRequest.getReferralCode().isEmpty()) {
            referredReferralCode = checkReferralCode(userRequest.getReferralCode());
            if (referredReferralCode == null) {
                throw new UserDetailsMissMatchException(Constants.INVALID_REFERRAL_CODE);
            }
        }

        if (userRequest.getUserType().equalsIgnoreCase("customer")) {
            UserEntity userEntity = userRepository.findUserByPhoneNumber(userRequest.getPhoneNumber());
            if (userEntity.getPhoneNumber().equals(userRequest.getPhoneNumber()) && userEntity.isOtpVerified()) {
                userEntity.setFirstName(userRequest.getFirstName());
                userEntity.setLastName(userRequest.getLastName());
                userEntity.setEmailAddress(userRequest.getEmailAddress());
                userEntity.setReferralCode(generator.referralCode());
                userEntity.setReferredReferralCode(referredReferralCode);
                userRepository.save(userEntity);
            } else {
                throw new UserDetailsMissMatchException(Constants.USER_DETAILS_MISS_MATCH);
            }

            RegisterResponse registerResponse = new RegisterResponse();
            registerResponse.setId(userEntity.getUserId());
            registerResponse.setMessage(Constants.REGISTER_SUCCESS);
            return registerResponse;
        } else {
            AffiliateUserEntity affiliateUser = affiliateUserRepository.findUserByPhoneNumber(userRequest.getPhoneNumber());
            if (affiliateUser.getPhoneNumber().equals(userRequest.getPhoneNumber()) && affiliateUser.isOtpVerified()) {
                affiliateUser.setFirstName(userRequest.getFirstName());
                affiliateUser.setLastName(userRequest.getLastName());
                affiliateUser.setEmailAddress(userRequest.getEmailAddress());
                affiliateUser.setReferralCode(generator.referralCode());
                affiliateUser.setReferredReferralCode(referredReferralCode);
                affiliateUserRepository.save(affiliateUser);
            } else {
                throw new UserDetailsMissMatchException(Constants.USER_DETAILS_MISS_MATCH);
            }
            RegisterResponse registerResponse = new RegisterResponse();
            registerResponse.setId(affiliateUser.getAffiliateUserId());
            registerResponse.setMessage(Constants.REGISTER_SUCCESS);
            return registerResponse;
        }
    }


    @Override
    public OTPResponse verifyOtp(ValidateOTP validateOTP) {
        if (validateOTP.getUserType().equalsIgnoreCase("customer")) {
            UserEntity userEntity = userRepository.findUserByPhoneNumber(validateOTP.getPhoneNumber());
            if (userEntity == null) {
                throw new UserNotFoundException(Constants.USER_NOT_FOUND);
            }
            if (userEntity.getOtpPassword().equals(validateOTP.getOtpPassword())) {
                userEntity.setOtpVerified(true);
                userRepository.save(userEntity);

                return otpResponseSuccess();
            } else {
                return otpResponseFailed();
            }
        } else {
            AffiliateUserEntity affiliateUser = affiliateUserRepository.findUserByPhoneNumber(validateOTP.getPhoneNumber());
            if (affiliateUser == null) {
                throw new UserNotFoundException(Constants.USER_NOT_FOUND);
            }

            if (affiliateUser.getOtpPassword().equals(validateOTP.getOtpPassword())) {
                affiliateUser.setOtpVerified(true);
                affiliateUserRepository.save(affiliateUser);

                return otpResponseSuccess();
            } else {
                return otpResponseFailed();
            }
        }
    }

    public OTPResponse generateOtp(OTPRequest otpRequest) {
        OTPResponse otpResponse = new OTPResponse();
        String otp = generateRandomOTP();
        String apiUrl = smsUrl + apiKey +
                VARIABLE_VALUES + otp +
                ROUTE_NUMBERS + otpRequest.getPhoneNumber();
        try {
            boolean success = sendOtp(apiUrl);
            if (success) {
                saveOTPToDB(otpRequest, otp);
                otpResponse.setMessage(Constants.OTP_SUCCESS);
                otpResponse.setStatusCode(HttpStatus.OK.value());
            } else {
                otpResponse.setMessage(Constants.OTP_FAILED);
                otpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            return otpResponse;
        } catch (Exception e) {
            log.info("exception :" + e.getMessage());
            otpResponse.setMessage("Exception while sending OTP.. ");
            otpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return otpResponse;
        }
    }

    private void saveOTPToDB(OTPRequest otpRequest, String otp) {
        if (otpRequest.getUserType().equalsIgnoreCase("customer")) {
            UserEntity userEntity = userRepository.findUserByPhoneNumber(otpRequest.getPhoneNumber());
            if (userEntity == null) {
                UserEntity user = new UserEntity();
                user.setUserId(generator.generateId(Constants.USER_ID));
                user.setUserType(otpRequest.getUserType());
                user.setPhoneNumber(otpRequest.getPhoneNumber());
                user.setOtpPassword(otp);
                user.setOtpVerified(false);
                userRepository.save(user);
            } else {
                userEntity.setOtpPassword(otp);
                userEntity.setOtpVerified(false);
                userRepository.save(userEntity);
            }
        } else {
            AffiliateUserEntity affiliateUser = affiliateUserRepository.findUserByPhoneNumber(otpRequest.getPhoneNumber());
            if (affiliateUser == null) {
                AffiliateUserEntity affiliate = new AffiliateUserEntity();
                affiliate.setAffiliateUserId(generator.generateId(Constants.AFFILIATE_USER_ID));
                affiliate.setUserType(otpRequest.getUserType());
                affiliate.setPhoneNumber(otpRequest.getPhoneNumber());
                affiliate.setOtpPassword(otp);
                affiliate.setOtpVerified(false);
                affiliateUserRepository.save(affiliate);
            } else {
                affiliateUser.setOtpPassword(otp);
                affiliateUser.setOtpVerified(false);
                affiliateUserRepository.save(affiliateUser);
            }
        }
    }

    private boolean sendOtp(String apiUrl) throws IOException, URISyntaxException {
        URI url = new URI(apiUrl);
        HttpURLConnection connection = createConnection(url.toURL());
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        return responseCode == 200;
    }

    protected HttpURLConnection createConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    private String generateRandomOTP() {
        Random random = new Random();
        int randomNumber = random.nextInt(999999 - 100000 + 1) + 100000;
        return String.valueOf(randomNumber);
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

    private OTPResponse otpResponseSuccess() {
        OTPResponse otpResponse = new OTPResponse();
        otpResponse.setMessage(Constants.OTP_VERIFIED_SUCCESSFULLY);
        otpResponse.setStatusCode(HttpStatus.OK.value());
        return otpResponse;
    }

    private OTPResponse otpResponseFailed() {
        OTPResponse otpResponse = new OTPResponse();
        otpResponse.setMessage(Constants.OTP_VERIFIED_FAILED);
        otpResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return otpResponse;
    }
}
