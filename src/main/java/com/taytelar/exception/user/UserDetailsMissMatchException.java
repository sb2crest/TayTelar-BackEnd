package com.taytelar.exception.user;

public class UserDetailsMissMatchException extends RuntimeException{
    public UserDetailsMissMatchException(String message){
        super(message);
    }
}
