package com.labnoratory.android_crypto;

import java.util.concurrent.CompletableFuture;

import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;

public interface Authenticator {

    CompletableFuture<BiometricPrompt.CryptoObject> authenticate(@Nullable BiometricPrompt.CryptoObject cryptoObject);

}
