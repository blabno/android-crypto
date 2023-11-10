package com.labnoratory.sample_app;

import com.labnoratory.android_crypto.AndroidCrypto;
import com.labnoratory.android_crypto.EncryptionResult;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.labnoratory.sample_app.ViewModelUtil.getBytes;
import static com.labnoratory.sample_app.ViewModelUtil.handleError;

public class EncryptSymmetricallyWithPasswordViewModel extends ViewModel {

    private static final String TAG = EncryptSymmetricallyWithPasswordViewModel.class.getSimpleName();

    protected final AndroidCrypto crypto = new AndroidCrypto();

    protected final MutableLiveData<String> iterations = new MutableLiveData<>("");
    protected final MutableLiveData<String> cipherText = new MutableLiveData<>("");
    protected final MutableLiveData<String> iv = new MutableLiveData<>("");
    protected final MutableLiveData<String> password = new MutableLiveData<>("");
    protected final MutableLiveData<String> payload = new MutableLiveData<>("");
    protected final MutableLiveData<String> salt = new MutableLiveData<>("");
    protected final MutableLiveData<String> status = new MutableLiveData<>();

    public MutableLiveData<String> getCipherText() {
        return cipherText;
    }

    public MutableLiveData<String> getIterations() {
        return iterations;
    }

    public MutableLiveData<String> getIv() {
        return iv;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public MutableLiveData<String> getPayload() {
        return payload;
    }

    public MutableLiveData<String> getSalt() {
        return salt;
    }

    public MutableLiveData<String> getStatus() {
        return status;
    }

    public void encrypt() {
        try {
            byte[] bytesToEncrypt = Optional.ofNullable(payload.getValue()).orElse("").getBytes(StandardCharsets.UTF_8);
            byte[] passwordBytes = Optional.ofNullable(password.getValue()).orElse("").getBytes(StandardCharsets.UTF_8);
            byte[] saltBytes = Optional.ofNullable(salt.getValue()).orElse("").getBytes(StandardCharsets.UTF_8);
            int i = Integer.parseInt(Optional.ofNullable(iterations.getValue()).orElse(""));
            EncryptionResult encryptionResult = crypto.encryptSymmetricallyWithPassword(passwordBytes, saltBytes, i, bytesToEncrypt);
            String value = Hex.encodeHexString(encryptionResult.getCipherText());
            cipherText.postValue(value);
            iv.postValue(Hex.encodeHexString(encryptionResult.getInitializationVector()));
            status.postValue("Data encrypted successfully");
        } catch (NumberFormatException e) {
            status.postValue("Iterations must be a number");
        } catch (Exception e) {
            handleError(TAG, status, e);
        }
    }

    public void decrypt() {
        try {
            byte[] cipherTextBytes = getBytes(cipherText, "Cipher text is invalid");
            byte[] ivBytes = getBytes(iv, "Cipher text is invalid");
            byte[] passwordBytes = Optional.ofNullable(password.getValue()).orElse("").getBytes(StandardCharsets.UTF_8);
            byte[] saltBytes = Optional.ofNullable(salt.getValue()).orElse("").getBytes(StandardCharsets.UTF_8);
            int i = Integer.parseInt(Optional.ofNullable(iterations.getValue()).orElse(""));
            byte[] decryptionResult = crypto.decryptSymmetricallyWithPassword(passwordBytes, saltBytes, i, cipherTextBytes, ivBytes);
            status.postValue(String.format("Decryption result:\nHex: %s\nString: %s", Hex.encodeHexString(decryptionResult), new String(decryptionResult)));
        } catch (NumberFormatException e) {
            status.postValue("Iterations must be a number");
        } catch (Exception e) {
            handleError(TAG, status, e);
        }
    }

}
