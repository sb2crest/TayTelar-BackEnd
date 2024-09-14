package com.taytelar.exception.otp;

public class UnknownUserTypeException extends RuntimeException{

    public UnknownUserTypeException(String message){
        super(message);
    }
}
