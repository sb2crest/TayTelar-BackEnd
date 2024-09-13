package com.taytelar.controller;

import com.taytelar.request.user.OTPRequest;
import com.taytelar.request.user.ValidateOTP;
import com.taytelar.request.user.UserRequest;
import com.taytelar.response.user.OTPResponse;
import com.taytelar.response.user.RegisterResponse;
import com.taytelar.service.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     * Endpoint to register a new user.
     *
     * @param userRequest The request object containing user registration details.
     * @return A ResponseEntity containing the RegisterResponse object and HTTP status 200 (OK) if registration is successful.
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> userRegister(@Valid @RequestBody UserRequest userRequest){
        RegisterResponse registerResponse = userService.register(userRequest);
        return ResponseEntity.status(HttpStatus.OK).body(registerResponse);
    }


    /**
     * Endpoint for OTP verification.
     *
     * @param validateOTP The request object containing phone number and the OTP entered by the user.
     *                    It is validated using @Valid to ensure the phone number and OTP are provided correctly.
     * @return A ResponseEntity containing the OTPResponse object and HTTP status 200 (OK) if OTP verification is successful.
     *         If the OTP verification fails, an appropriate error response will be returned.
     */
    @PostMapping("/verifyOtp")
    public ResponseEntity<OTPResponse> verifyOtp(@Valid @RequestBody ValidateOTP validateOTP){
        OTPResponse response = userService.verifyOtp(validateOTP);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint to generate an OTP and send it to the user's phone number.
     *
     * @param otpRequest The phone number for which the OTP should be generated. It must not be blank.
     * @return A ResponseEntity containing the OTPResponse object and HTTP status 200 (OK) if OTP generation is successful.
     *         This response object typically contains the message indicating that the OTP has been sent.
     */
    @PostMapping("/sendOtp")
    public  ResponseEntity<OTPResponse> generateOtp(@Valid @RequestBody OTPRequest otpRequest) {
        OTPResponse response = userService.generateOtp(otpRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
