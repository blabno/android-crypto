package com.labnoratory.sample_app;

import android.app.Activity;

import com.labnoratory.android_crypto.AndroidAuthenticator;
import com.labnoratory.android_crypto.AndroidCrypto;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import androidx.lifecycle.MutableLiveData;

import static com.labnoratory.sample_app.ViewModelUtil.getBytes;
import static com.labnoratory.sample_app.ViewModelUtil.handleError;

public class EncryptSymmetricallyViewModel extends AbstractEncryptViewModel {

    private static final String TAG = EncryptSymmetricallyViewModel.class.getSimpleName();

    private static final String KEY_NAME = KeyName.SymmetricEncryption.name();

    private final MutableLiveData<String> iv = new MutableLiveData<>("");

    public MutableLiveData<String> getIv() {
        return iv;
    }

    public void encrypt(Activity activity) {
        try {
            byte[] bytesToEncrypt = Optional.ofNullable(payload.getValue()).orElse("").getBytes(StandardCharsets.UTF_8);
            crypto.encryptSymmetrically(KEY_NAME, bytesToEncrypt, new AndroidAuthenticator(activity))
                    .whenComplete((encryptionResult, throwable) -> {
                        if (null != throwable) {
                            handleError(TAG, status, throwable);
                            return;
                        }
                        cipherText.postValue(Hex.encodeHexString(encryptionResult.getCipherText()));
                        iv.postValue(Hex.encodeHexString(encryptionResult.getInitializationVector()));
                        status.postValue("Data encrypted successfully");
                    });
        } catch (Exception e) {
            handleError(TAG, status, e);
        }
    }

    public void decrypt(Activity activity) {
        try {
            byte[] cipherTextBytes = getBytes(cipherText, "Cipher text is invalid");
            byte[] ivBytes = getBytes(iv, "Initialization vector is invalid");
            crypto.decryptSymmetrically(KEY_NAME, cipherTextBytes, ivBytes, new AndroidAuthenticator(activity))
                    .whenComplete((decryptionResult, throwable) -> {
                        if (null != throwable) {
                            handleError(TAG, status, throwable);
                            return;
                        }
                        status.postValue(String.format("Decryption result:\nHex: %s\nString: %s", Hex.encodeHexString(decryptionResult), new String(decryptionResult)));
                    });
        } catch (Exception e) {
            handleError(TAG, status, e);
        }
    }


    @Override
    protected String getKeyName() {
        return KEY_NAME;
    }

    @Override
    protected void doCreateKey(AndroidCrypto.AccessLevel accessLevel) throws Exception {
        crypto.createSymmetricEncryptionKey(getKeyName(), accessLevel, false);
    }

}
