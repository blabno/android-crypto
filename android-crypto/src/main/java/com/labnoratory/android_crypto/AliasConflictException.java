package com.labnoratory.android_crypto;

public class AliasConflictException extends RuntimeException {

    public AliasConflictException() {
        super("A key for given alias already exists");
    }
}
