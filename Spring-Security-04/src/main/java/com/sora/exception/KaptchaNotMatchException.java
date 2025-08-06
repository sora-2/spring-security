package com.sora.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

import javax.security.sasl.AuthenticationException;

public class KaptchaNotMatchException extends AuthenticationException {
    public KaptchaNotMatchException(String detail, Throwable ex) {
        super(detail, ex);
    }

    public KaptchaNotMatchException() {
        super();
    }

    public KaptchaNotMatchException(String detail) {
        super(detail);
    }
}
