package com.taytelar.service.serviceImplementation.otp;

import com.taytelar.entity.affiliate.AffiliateUserEntity;
import com.taytelar.entity.otp.OTPEntity;
import com.taytelar.entity.user.UserEntity;
import com.taytelar.exception.otp.OtpNotFoundException;
import com.taytelar.exception.otp.UnknownUserTypeException;
import com.taytelar.exception.user.UserAccountAlreadyExistException;
import com.taytelar.exception.user.UserNotFoundException;
import com.taytelar.repository.AffiliateUserRepository;
import com.taytelar.repository.OTPRepository;
import com.taytelar.repository.UserRepository;
import com.taytelar.request.user.OTPRequest;
import com.taytelar.request.user.ValidateOTP;
import com.taytelar.response.user.OTPResponse;
import com.taytelar.service.service.otp.OTPService;
import com.taytelar.util.Constants;
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


    @Override
    public OTPResponse generateOtp(OTPRequest otpRequest) {
        if (otpRequest.getRequestType().equalsIgnoreCase(Constants.LOGIN)) {
            if (otpRequest.getUserType().equalsIgnoreCase(Constants.CUSTOMER)) {
                UserEntity userEntity = userRepository.findUserByPhoneNumber(otpRequest.getPhoneNumber());
                if (userEntity == null) {
                    throw new UserAccountAlreadyExistException(Constants.USER_ACCOUNT_NOT_EXIST);
                }
            } else {
                AffiliateUserEntity affiliateUser = affiliateUserRepository.findUserByPhoneNumber(otpRequest.getPhoneNumber());
                if (affiliateUser == null) {
                    throw new UserAccountAlreadyExistException(Constants.AFFILIATE_USER_ACCOUNT_NOT_EXIST);
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
        String apiUrl = smsUrl + apiKey +
                VARIABLE_VALUES + otp +
                ROUTE_NUMBERS + otpRequest.getPhoneNumber();

        log.info("smsUrl: {}", smsUrl);
        log.info("apiKey: {}", apiKey);
        log.info("apiUrl: {}", apiUrl);

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

    @Override
    public OTPResponse verifyOtp(ValidateOTP validateOTP) {
        OTPEntity otpEntity = otpRepository.findByPhoneNumber(validateOTP.getPhoneNumber());
        List<String> types = Arrays.stream(otpEntity.getUserType().split(",")).toList();
        boolean userType = types.contains(validateOTP.getUserType());

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
            otpRepository.save(otpEntity);
        } else {
            entity.setOtpCode(otp);
            entity.setOtpVerified(false);
            if (otpRequest.getUserType().equalsIgnoreCase(Constants.CUSTOMER) && entity.getUserType().equalsIgnoreCase(Constants.CUSTOMER)) {
                entity.setUserType(otpRequest.getUserType());
            } else if (entity.getUserType().equalsIgnoreCase(Constants.AFFILIATE) && otpRequest.getUserType().equalsIgnoreCase(Constants.AFFILIATE)) {
                entity.setUserType(otpRequest.getUserType());
            } else {
                entity.setUserType(entity.getUserType() + "," + otpRequest.getUserType());
            }

            otpRepository.save(entity);
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
