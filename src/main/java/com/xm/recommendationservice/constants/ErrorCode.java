package com.xm.recommendationservice.constants;

public enum ErrorCode {

    CURRENCY_NOT_SUPPORTED_YET(-1, "This cryptocurrency is not yet supported by the service"),
    NO_SUCH_ELEMENT(-2, "No such element"),
    COMMON_IO_EXCEPTION(-3, "Something went wrong while reading the price data." +
            " Contact your administrator.");

    public final int code;
    public final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
