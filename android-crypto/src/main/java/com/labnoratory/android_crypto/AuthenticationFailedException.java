package com.labnoratory.android_crypto;

import androidx.annotation.NonNull;

public class AuthenticationFailedException extends RuntimeException {

    @NonNull
    @Override
    public String toString() {
        return "AuthenticationFailedException";
    }
}
