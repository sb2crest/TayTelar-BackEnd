package com.taytelar.exception.affiliate;

public class FailedToSendOtpException extends RuntimeException{
    public FailedToSendOtpException(String message) {
        super(message);
    }
}
