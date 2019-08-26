package com.in28minutes.microservices.limitsservice.auth.exception;


/**
 * Created by aayush on 31/10/2018.
 */
public class CIAuthenticationException extends RuntimeException {

    public CIAuthenticationException(int httpStatus, String errorReason, String message) {
        super("HTTP CODE = " + httpStatus + " " +  errorReason + message);
    }
}
