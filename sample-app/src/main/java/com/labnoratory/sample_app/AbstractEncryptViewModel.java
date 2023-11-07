package com.labnoratory.sample_app;

import android.util.Log;

import com.labnoratory.android_crypto.AndroidCrypto;

import java.security.KeyStoreException;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.labnoratory.sample_app.ViewModelUtil.handleError;

public abstract class AbstractEncryptViewModel extends ViewModel {

    private static final String TAG = AuthenticateViewModel.class.getSimpleName();

    protected final AndroidCrypto crypto = new AndroidCrypto();

    protected final MutableLiveData<Boolean> authenticationRequired = new MutableLiveData<>(false);
    protected final MutableLiveData<String> cipherText = new MutableLiveData<>("");
    protected final MutableLiveData<Boolean> keyExists = new MutableLiveData<>(false);
    protected final MutableLiveData<String> payload = new MutableLiveData<>("");
    protected final MutableLiveData<String> status = new MutableLiveData<>();

    public AbstractEncryptViewModel() {
        checkKey();
    }

    public MutableLiveData<Boolean> getAuthenticationRequired() {
        return authenticationRequired;
    }

    public MutableLiveData<String> getCipherText() {
        return cipherText;
    }

    public MutableLiveData<Boolean> getKeyExists() {
        return keyExists;
    }

    public MutableLiveData<String> getPayload() {
        return payload;
    }

    public MutableLiveData<String> getStatus() {
        return status;
    }

    public void createKey() {
        try {
            AndroidCrypto.AccessLevel accessLevel = Boolean.TRUE.equals(authenticationRequired.getValue()) ? AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED : AndroidCrypto.AccessLevel.ALWAYS;
            doCreateKey(accessLevel);
            status.postValue("Encryption key created successfully");
            checkKey();
        } catch (Exception e) {
            handleError(TAG, status, e);
        }
    }

    public void removeKey() {
        try {
            crypto.deleteKey(getKeyName());
            status.postValue("Key removed successfully");
            checkKey();
        } catch (Exception e) {
            handleError(TAG, status, e);
        }
    }

    protected abstract String getKeyName();

    protected abstract void doCreateKey(AndroidCrypto.AccessLevel accessLevel) throws Exception;

    protected void checkKey() {
        try {
            keyExists.postValue(crypto.containsKey(getKeyName()));
        } catch (KeyStoreException e) {
            Log.e(TAG, "Failed to check if key exists", e);
            keyExists.postValue(false);
        }
    }
}
