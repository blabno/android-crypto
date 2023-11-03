package com.labnoratory.android_crypto;

public class AliasConflictException extends RuntimeException {

    public AliasConflictException() {
        super("Some key for given alias already exists");
    }
}
