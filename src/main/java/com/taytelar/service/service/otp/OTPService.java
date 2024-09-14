package com.taytelar.service.service.otp;

import com.taytelar.request.user.OTPRequest;
import com.taytelar.request.user.ValidateOTP;
import com.taytelar.response.user.OTPResponse;

public interface OTPService {
    OTPResponse verifyOtp(ValidateOTP validateOTP);

    OTPResponse generateOtp(OTPRequest otpRequest);
}
