package com.taytelar.service.service.user;

import com.taytelar.request.user.OTPRequest;
import com.taytelar.request.user.ValidateOTP;
import com.taytelar.request.user.UserRequest;
import com.taytelar.response.user.OTPResponse;
import com.taytelar.response.user.RegisterResponse;


public interface UserService {
    RegisterResponse register(UserRequest userRequest);

    OTPResponse verifyOtp(ValidateOTP validateOTP);

    OTPResponse generateOtp(OTPRequest otpRequest);
}
