package com.labnoratory.sample_app;

import android.app.Activity;
import android.util.Log;

import com.labnoratory.android_crypto.AndroidAuthenticator;
import com.labnoratory.android_crypto.AndroidCrypto;
import com.labnoratory.android_crypto.KeyNotFoundException;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Optional;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.labnoratory.sample_app.ViewModelUtil.getBytes;
import static com.labnoratory.sample_app.ViewModelUtil.handleError;

public class SignViewModel extends ViewModel {

    private static final String TAG = SignViewModel.class.getSimpleName();

    private static final String KEY_NAME = KeyName.Sign.name();

    private final AndroidCrypto crypto = new AndroidCrypto();

    private final MutableLiveData<Boolean> authenticationRequired = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> keyExists = new MutableLiveData<>(false);
    private final MutableLiveData<String> payload = new MutableLiveData<>("");
    private final MutableLiveData<String> publicKey = new MutableLiveData<>("");
    private final MutableLiveData<String> signature = new MutableLiveData<>("");
    private final MutableLiveData<String> status = new MutableLiveData<>();

    public SignViewModel() {
        checkKey();
    }

    public MutableLiveData<Boolean> getAuthenticationRequired() {
        return authenticationRequired;
    }

    public MutableLiveData<Boolean> getKeyExists() {
        return keyExists;
    }

    public MutableLiveData<String> getPayload() {
        return payload;
    }

    public MutableLiveData<String> getPublicKey() {
        return publicKey;
    }

    public MutableLiveData<String> getSignature() {
        return signature;
    }

    public MutableLiveData<String> getStatus() {
        return status;
    }

    public void createKey() {
        try {
            AndroidCrypto.AccessLevel accessLevel = Boolean.TRUE.equals(authenticationRequired.getValue()) ? AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED : AndroidCrypto.AccessLevel.ALWAYS;
            crypto.createSigningKey(KEY_NAME, accessLevel, false);
            status.postValue("Signing key created successfully");
            checkKey();
        } catch (Exception e) {
            handleError(TAG, status, e);
        }
    }

    public void removeKey() {
        try {
            crypto.deleteKey(KEY_NAME);
            status.postValue("Key removed successfully");
            checkKey();
        } catch (Exception e) {
            handleError(TAG, status, e);
        }
    }

    public void sign(Activity activity) {
        try {
            byte[] payloadBytes = Optional.ofNullable(payload.getValue()).orElse("").getBytes(StandardCharsets.UTF_8);
            crypto.sign(KEY_NAME, payloadBytes, new AndroidAuthenticator(activity)).whenComplete((signatureBytes, throwable) -> {
                if (null != throwable) {
                    handleError(TAG, status, throwable);
                    return;
                }
                signature.postValue(Hex.encodeHexString(signatureBytes));
                try {
                    publicKey.postValue(Hex.encodeHexString(crypto.getPublicKey(KEY_NAME).getEncoded()));
                    status.postValue("Data signed successfully");
                } catch (KeyNotFoundException e) {
                    handleError(TAG, status, e);
                }
            });
        } catch (Exception e) {
            handleError(TAG, status, e);
        }
    }

    public void verifySignature() {
        try {
            String publicKeyString = Optional.ofNullable(publicKey.getValue()).orElse("");
            byte[] payloadBytes = Optional.ofNullable(payload.getValue()).orElse("").getBytes(StandardCharsets.UTF_8);
            byte[] signatureBytes = getBytes(signature, "Signature is invalid");
            byte[] publicKeyBytes;
            boolean signatureValid;
            if ("".equals(publicKeyString.trim())) {
                signatureValid = crypto.verifySignature(KEY_NAME, payloadBytes, signatureBytes);
            } else {
                publicKeyBytes = Hex.decodeHex(publicKeyString);
                PublicKey publicKey = decodePublicKeyASN1(publicKeyBytes);
                signatureValid = crypto.verifySignature(publicKey, payloadBytes, signatureBytes);
            }
            status.postValue(signatureValid ? "Signature valid" : "Signature invalid");
        } catch (Exception e) {
            handleError(TAG, status, e);
        }
    }

    private void checkKey() {
        try {
            keyExists.postValue(crypto.containsKey(KEY_NAME));
        } catch (KeyStoreException e) {
            Log.e(TAG, "Failed to check if key exists", e);
            keyExists.postValue(false);
        }
    }

    private static PublicKey decodePublicKeyASN1(byte[] publicKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String algorithm = SubjectPublicKeyInfo.getInstance(publicKeyBytes).getAlgorithm().getAlgorithm().getId();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(keySpec);
    }

}
