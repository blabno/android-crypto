package com.labnoratory.android_crypto;

import androidx.annotation.NonNull;

public class AuthenticationErrorException extends RuntimeException {

    private final int errorCode;
    private final CharSequence errString;

    public AuthenticationErrorException(int errorCode, CharSequence errString) {
        this.errorCode = errorCode;
        this.errString = errString;
    }

    /** @noinspection unused*/
    public int getErrorCode() {
        return errorCode;
    }

    /** @noinspection unused*/
    public CharSequence getErrString() {
        return errString;
    }

    @NonNull
    @Override
    public String toString() {
        return "AuthenticationErrorException{" +
                "errorCode=" + errorCode +
                ", errString=" + errString +
                '}';
    }
}
