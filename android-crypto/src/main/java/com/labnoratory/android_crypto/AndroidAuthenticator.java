package com.labnoratory.android_crypto;

import android.app.Activity;
import android.util.Log;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import static com.labnoratory.android_crypto.Options.getString;

public class AndroidAuthenticator implements Authenticator{

    private static final String BIOMETRY_TITLE = "Biometric Authentication";
    private static final String BIOMETRY_SUBTITLE = "Authentication required";
    private static final String BIOMETRY_DESCRIPTION = "";
    private static final String BIOMETRY_NEGATIVE_BUTTON_TEXT = "Cancel";

    private static final String TAG = "Authenticator";

    private final Activity activity;
    private final Map<String, Object> options;

    public AndroidAuthenticator(@NonNull Activity activity) {
        this(activity, Collections.emptyMap());
    }

    public AndroidAuthenticator(@NonNull Activity activity, @NonNull Map<String, Object> options) {
        this.activity = activity;
        this.options = options;
    }

    @Override
    public CompletableFuture<BiometricPrompt.CryptoObject> authenticate(@Nullable BiometricPrompt.CryptoObject cryptoObject) {
        CompletableFuture<BiometricPrompt.CryptoObject> future = new CompletableFuture<>();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Executor executor = Executors.newSingleThreadExecutor();
                    String title = getString(options, "biometryTitle", BIOMETRY_TITLE);
                    String subTitle = getString(options, "biometrySubTitle", BIOMETRY_SUBTITLE);
                    String description = getString(options, "biometryDescription", BIOMETRY_DESCRIPTION);
                    String negativeButtonText = getString(options, "negativeButtonText", BIOMETRY_NEGATIVE_BUTTON_TEXT);

                    AtomicReference<BiometricPrompt> biometricPromptAtomicReference = new AtomicReference<>();

                    BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                            .setTitle(title)
                            .setSubtitle(subTitle)
                            .setDescription(description)
                            .setNegativeButtonText(negativeButtonText)
                            .build();

                    BiometricPrompt.AuthenticationCallback authCallback = new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            BiometricPrompt biometricPrompt = biometricPromptAtomicReference.get();
                            if (null == biometricPrompt) {
                                Log.w(TAG, String.format("Authentication error (%d) but no reference to biometric prompt found", errorCode));
                            } else {
                                biometricPrompt.cancelAuthentication();
                            }
                            future.completeExceptionally(new AuthenticationErrorException(errorCode, errString));
                        }

                        @Override
                        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            future.complete(result.getCryptoObject());
                        }
                    };

                    BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) activity, executor, authCallback);
                    biometricPromptAtomicReference.set(biometricPrompt);
                    if (null == cryptoObject) {
                        biometricPrompt.authenticate(promptInfo);
                    } else {
                        biometricPrompt.authenticate(promptInfo, cryptoObject);
                    }
                } catch (Exception e) {
                    Log.e(TAG, String.format("Failed to authenticate with biometry due to: %s", e.getMessage()), e);
                }
            }
        });
        return future;
    }
}
