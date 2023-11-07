package com.labnoratory.sample_app;

import android.app.Activity;

import com.labnoratory.android_crypto.AndroidCrypto;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.labnoratory.sample_app.ViewModelUtil.getBytes;
import static com.labnoratory.sample_app.ViewModelUtil.handleError;

public class EncryptAsymmetricallyViewModel extends AbstractEncryptViewModel {

    private static final String TAG = AuthenticateViewModel.class.getSimpleName();

    private static final String KEY_NAME = KeyName.AsymmetricEncryption.name();

    public void encrypt() {
        try {
            byte[] bytesToEncrypt = Optional.ofNullable(payload.getValue()).orElse("").getBytes(StandardCharsets.UTF_8);
            byte[] cipherTextBytes = crypto.encryptAsymmetrically(getKeyName(), bytesToEncrypt);
            cipherText.postValue(Hex.encodeHexString(cipherTextBytes));
            status.postValue("Data encrypted successfully");
        } catch (Exception e) {
            handleError(TAG, status, e);
        }
    }

    public void decrypt(Activity activity) {
        try {
            byte[] cipherTextBytes = getBytes(cipherText, "Cipher text is invalid");
            crypto.decryptAsymmetrically(activity, getKeyName(), cipherTextBytes)
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

    protected void doCreateKey(AndroidCrypto.AccessLevel accessLevel) throws Exception {
        crypto.createAsymmetricEncryptionKey(getKeyName(), accessLevel, false);
    }

    @Override
    protected String getKeyName() {
        return KEY_NAME;
    }
}
