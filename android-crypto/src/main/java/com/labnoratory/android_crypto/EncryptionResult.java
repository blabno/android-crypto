package com.labnoratory.android_crypto;

import java.util.Arrays;

import androidx.annotation.NonNull;

public class EncryptionResult {
    private final byte[] initializationVector;
    private final byte[] cipherText;

    public EncryptionResult(byte[] cipherText, byte[] initializationVector) {
        this.cipherText = cipherText;
        this.initializationVector = initializationVector;
    }

    public byte[] getInitializationVector() {
        return initializationVector;
    }

    public byte[] getCipherText() {
        return cipherText;
    }

    @NonNull
    @Override
    public String toString() {
        return "EncryptionResult{" +
                "initializationVector=" + Arrays.toString(initializationVector) +
                ", cipherText=" + Arrays.toString(cipherText) +
                '}';
    }
}
