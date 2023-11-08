package com.labnoratory.android_crypto;

public class AliasConflictException extends Exception {

    public AliasConflictException() {
        super("A key for given alias already exists");
    }
}
