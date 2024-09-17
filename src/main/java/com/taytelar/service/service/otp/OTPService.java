package com.taytelar.service.service.otp;

import com.taytelar.request.otp.OTPRequest;
import com.taytelar.request.otp.ValidateOTP;
import com.taytelar.response.otp.OTPResponse;

public interface OTPService {
    OTPResponse verifyOtp(ValidateOTP validateOTP);

    OTPResponse generateOtp(OTPRequest otpRequest);
}
