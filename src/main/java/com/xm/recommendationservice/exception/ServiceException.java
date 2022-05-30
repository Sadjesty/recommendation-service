package com.xm.recommendationservice.exception;

import com.xm.recommendationservice.constants.ErrorCode;

public class ServiceException extends Exception {

    private final ErrorCode errorCode;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.message);
        this.errorCode = errorCode;
    }

    public ServiceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.message, cause);
        this.errorCode = errorCode;
    }
}
