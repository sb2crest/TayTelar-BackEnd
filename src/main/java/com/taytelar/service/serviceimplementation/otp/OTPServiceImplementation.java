package com.taytelar.service.serviceimplementation.otp;

import com.taytelar.entity.otp.OTPEntity;
import com.taytelar.exception.otp.UnknownUserTypeException;
import com.taytelar.exception.user.UserAccountAlreadyExistException;
import com.taytelar.exception.user.UserAccountNotExistException;
import com.taytelar.repository.admin.AdminRepository;
import com.taytelar.repository.user.AffiliateUserRepository;
import com.taytelar.repository.otp.OTPRepository;
import com.taytelar.repository.user.UserRepository;
import com.taytelar.request.otp.OTPRequest;
import com.taytelar.request.otp.ValidateOTP;
import com.taytelar.response.otp.OTPResponse;
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
    private final AdminRepository adminRepository;
    private final Random random = new Random();
    private static final String VARIABLE_VALUES = "&variables_values=";
    private static final String ROUTE_NUMBERS = "&route=otp&numbers=";

    @Value("${api.key}")
    private String apiKey;

    @Value("${sms.url}")
    private String smsUrl;

    @Override
    public OTPResponse generateOtp(OTPRequest otpRequest) {
        try {
            validateUser(otpRequest);
            String otp = generateRandomOTP();
            log.info("Generated OTP : {}", otp);

            String apiUrl = buildApiUrl(otpRequest.getPhoneNumber(), otp);
            boolean success = sendOtp(apiUrl);

            return buildOtpResponse(success, otpRequest);
        } catch (Exception e) {
            log.error("Exception while generating OTP: {}", e.getMessage());
            return buildErrorResponse();
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

    private void validateUser(OTPRequest otpRequest) {
        if (Constants.LOGIN.equalsIgnoreCase(otpRequest.getRequestType())) {
            if (Constants.CUSTOMER.equalsIgnoreCase(otpRequest.getUserType())) {
                validateUserExists(userRepository.findUserByPhoneNumber(otpRequest.getPhoneNumber()), Constants.USER_ACCOUNT_NOT_EXIST);
            } else if(Constants.AFFILIATE.equalsIgnoreCase(otpRequest.getUserType())) {
                validateUserExists(affiliateUserRepository.findUserByPhoneNumber(otpRequest.getPhoneNumber()), Constants.AFFILIATE_USER_ACCOUNT_NOT_EXIST);
            } else if (Constants.ADMIN.equalsIgnoreCase(otpRequest.getUserType())) {
                validateUserExists(adminRepository.findUserByPhoneNumber(otpRequest.getPhoneNumber()), Constants.ADMIN_USER_ACCOUNT_NOT_EXIST);
            }
        } else if (Constants.REGISTER.equalsIgnoreCase(otpRequest.getRequestType())) {
            if (Constants.CUSTOMER.equalsIgnoreCase(otpRequest.getUserType())) {
                validateUserNotExists(userRepository.findUserByPhoneNumber(otpRequest.getPhoneNumber()), Constants.USER_ALREADY_EXIST);
            } else if(Constants.AFFILIATE.equalsIgnoreCase(otpRequest.getUserType())) {
                validateUserNotExists(affiliateUserRepository.findUserByPhoneNumber(otpRequest.getPhoneNumber()), Constants.AFFILIATE_USER_ALREADY_EXIST);
            } else if (Constants.ADMIN.equalsIgnoreCase(otpRequest.getUserType())) {
                validateUserNotExists(adminRepository.findUserByPhoneNumber(otpRequest.getPhoneNumber()), Constants.ADMIN_USER_ALREADY_EXIST);
            }
        } else {
            throw new UnknownUserTypeException(Constants.UNKNOWN_USER_TYPE);
        }
    }

    private void validateUserExists(Object user, String exceptionMessage) {
        if (user == null) {
            throw new UserAccountNotExistException(exceptionMessage);
        }
    }

    private void validateUserNotExists(Object user, String exceptionMessage) {
        if (user != null) {
            throw new UserAccountAlreadyExistException(exceptionMessage);
        }
    }

    private String buildApiUrl(String phoneNumber, String otp) {
        return smsUrl + apiKey +
                VARIABLE_VALUES + otp +
                ROUTE_NUMBERS + phoneNumber;
    }

    private boolean sendOtp(String apiUrl) throws IOException, URISyntaxException {
        URI url = new URI(apiUrl);
        HttpURLConnection connection = createConnection(url.toURL());
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        log.info("Response code while sending OTP: {}", responseCode);
        return responseCode == 200;
    }

    private OTPResponse buildOtpResponse(boolean success, OTPRequest otpRequest) {
        OTPResponse otpResponse = new OTPResponse();
        if (success) {
            saveOTPToDB(otpRequest, generateRandomOTP());
            otpResponse.setMessage(Constants.OTP_SUCCESS);
            otpResponse.setStatusCode(HttpStatus.OK.value());
        } else {
            otpResponse.setMessage(Constants.OTP_FAILED);
            otpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        log.info("OTP response: {}", otpResponse);
        return otpResponse;
    }

    private OTPResponse buildErrorResponse() {
        OTPResponse otpResponse = new OTPResponse();
        otpResponse.setMessage("Exception while sending OTP.. ");
        otpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return otpResponse;
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

    protected HttpURLConnection createConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    private String generateRandomOTP() {
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
