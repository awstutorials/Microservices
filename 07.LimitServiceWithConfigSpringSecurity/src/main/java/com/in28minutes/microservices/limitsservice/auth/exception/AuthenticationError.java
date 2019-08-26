package com.in28minutes.microservices.limitsservice.auth.exception;

/**
 * Created by aayush on 31/10/2018.
 */
public class AuthenticationError {
    public static final String NOT_VALID_JSON_WEB_TOKEN = "Not a valid json web token";
    public static final String TOKEN_EXPIRED = " Aws Jwt Token has expired.";
    public static final String TOKEN_TYPE_INCORRECT = "We only accept ID tokens";
    public static final String TOKEN_SIGNATURE_ENCRYPTION_ERROR = "Cannot verify token signature or encryption";
}
