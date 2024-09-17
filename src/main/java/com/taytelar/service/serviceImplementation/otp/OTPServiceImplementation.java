package com.taytelar.service.serviceImplementation.otp;

import com.taytelar.entity.affiliate.AffiliateUserEntity;
import com.taytelar.entity.otp.OTPEntity;
import com.taytelar.entity.user.UserEntity;
import com.taytelar.exception.otp.UnknownUserTypeException;
import com.taytelar.exception.user.UserAccountAlreadyExistException;
import com.taytelar.exception.user.UserAccountNotExistException;
import com.taytelar.repository.AffiliateUserRepository;
import com.taytelar.repository.OTPRepository;
import com.taytelar.repository.UserRepository;
import com.taytelar.request.otp.OTPRequest;
import com.taytelar.request.otp.ValidateOTP;
import com.taytelar.response.otp.OTPResponse;
import com.taytelar.service.service.otp.OTPService;
import com.taytelar.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class OTPServiceImplementation implements OTPService {

    private final OTPRepository otpRepository;

    private final UserRepository userRepository;

    private final AffiliateUserRepository affiliateUserRepository;
    private final static String VARIABLE_VALUES = "&variables_values=";
    private final static String ROUTE_NUMBERS = "&route=otp&numbers=";
    @Value("${api.key}")
    private String apiKey;
    @Value("${sms.url}")
    private String smsUrl;
    private Logger lo;


    @Override
    public OTPResponse generateOtp(OTPRequest otpRequest) {
        if (otpRequest.getRequestType().equalsIgnoreCase(Constants.LOGIN)) {
            if (otpRequest.getUserType().equalsIgnoreCase(Constants.CUSTOMER)) {
                UserEntity userEntity = userRepository.findUserByPhoneNumber(otpRequest.getPhoneNumber());
                if (userEntity == null) {
                    throw new UserAccountNotExistException(Constants.USER_ACCOUNT_NOT_EXIST);
                }
            } else {
                AffiliateUserEntity affiliateUser = affiliateUserRepository.findUserByPhoneNumber(otpRequest.getPhoneNumber());
                if (affiliateUser == null) {
                    throw new UserAccountNotExistException(Constants.AFFILIATE_USER_ACCOUNT_NOT_EXIST);
                }
            }
        } else if (otpRequest.getRequestType().equalsIgnoreCase(Constants.REGISTER)) {
            if (otpRequest.getUserType().equalsIgnoreCase(Constants.CUSTOMER)) {
                UserEntity userEntity = userRepository.findUserByPhoneNumber(otpRequest.getPhoneNumber());
                if (userEntity != null) {
                    throw new UserAccountAlreadyExistException(Constants.USER_ALREADY_EXIST);
                }
            } else {
                AffiliateUserEntity affiliateUser = affiliateUserRepository.findUserByPhoneNumber(otpRequest.getPhoneNumber());
                if (affiliateUser != null) {
                    throw new UserAccountAlreadyExistException(Constants.AFFILIATE_USER_ALREADY_EXIST);
                }
            }
        } else {
            throw new UnknownUserTypeException(Constants.UNKNOWN_USER_TYPE);
        }


        OTPResponse otpResponse = new OTPResponse();
        String otp = generateRandomOTP();

        log.info("Generated OTP : {}", otp);
        String apiUrl = smsUrl + apiKey +
                VARIABLE_VALUES + otp +
                ROUTE_NUMBERS + otpRequest.getPhoneNumber();

        log.info("smsUrl: {}", smsUrl);
        log.info("apiKey: {}", apiKey);
        log.info("apiUrl: {}", apiUrl);

        try {
            boolean success = sendOtp(apiUrl);
            log.info("Send OTP success or failed: {}", success);
            if (success) {
                saveOTPToDB(otpRequest, otp);
                otpResponse.setMessage(Constants.OTP_SUCCESS);
                otpResponse.setStatusCode(HttpStatus.OK.value());
            } else {
                otpResponse.setMessage(Constants.OTP_FAILED);
                otpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

            log.info("OTP response: {}", otpResponse);
            return otpResponse;
        } catch (Exception e) {
            log.info("exception :" + e.getMessage());
            otpResponse.setMessage("Exception while sending OTP.. ");
            otpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return otpResponse;
        }
    }

    @Override
    public OTPResponse verifyOtp(ValidateOTP validateOTP) {
        OTPEntity otpEntity = otpRepository.findByPhoneNumber(validateOTP.getPhoneNumber());
        List<String> types = Arrays.stream(otpEntity.getUserType().split(",")).toList();
        boolean userType = types.contains(validateOTP.getUserType());
        log.info("User Type : {} ", userType);

        if (otpEntity.getOtpCode().equals(validateOTP.getOtpPassword()) && userType) {
            otpEntity.setOtpVerified(true);
            otpRepository.save(otpEntity);
            return otpResponseSuccess();
        } else {
            return otpResponseFailed();
        }
    }

    private void saveOTPToDB(OTPRequest otpRequest, String otp) {
        OTPEntity entity = otpRepository.findByPhoneNumber(otpRequest.getPhoneNumber());
        if (entity == null) {
            OTPEntity otpEntity = new OTPEntity();
            otpEntity.setPhoneNumber(otpRequest.getPhoneNumber());
            otpEntity.setOtpCode(otp);
            otpEntity.setOtpVerified(false);
            otpEntity.setUserType(otpRequest.getUserType());

            log.info("OTP entity: {}", otpEntity);
            otpRepository.save(otpEntity);
        } else {
            entity.setOtpCode(otp);
            entity.setOtpVerified(false);

            List<String> userTypes = Arrays.asList(entity.getUserType().split(","));
            if (!userTypes.contains(otpRequest.getUserType())) {
                entity.setUserType(entity.getUserType() + "," + otpRequest.getUserType());
            }
            log.info("Updated OTP entity: {}", entity);
            otpRepository.save(entity);
        }
    }

    private boolean sendOtp(String apiUrl) throws IOException, URISyntaxException {
        URI url = new URI(apiUrl);
        HttpURLConnection connection = createConnection(url.toURL());
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        log.info("Response code while sending OTP: {}", responseCode);
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

    private OTPResponse otpResponseSuccess() {
        OTPResponse otpResponse = new OTPResponse();
        otpResponse.setMessage(Constants.OTP_VERIFIED_SUCCESSFULLY);
        otpResponse.setStatusCode(HttpStatus.OK.value());

        log.info("OTP Success Response {}", otpResponse);
        return otpResponse;
    }

    private OTPResponse otpResponseFailed() {
        OTPResponse otpResponse = new OTPResponse();
        otpResponse.setMessage(Constants.OTP_VERIFIED_FAILED);
        otpResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());

        log.info("OTP Failed Response {}", otpResponse);
        return otpResponse;
    }
}
