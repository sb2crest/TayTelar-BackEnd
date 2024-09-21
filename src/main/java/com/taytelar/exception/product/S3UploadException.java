package com.taytelar.exception.product;

public class S3UploadException extends RuntimeException {
    public S3UploadException(String message) {
        super(message);
    }
}