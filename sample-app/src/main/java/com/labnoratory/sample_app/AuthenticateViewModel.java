package com.labnoratory.sample_app;

import android.app.Activity;

import com.labnoratory.android_crypto.AndroidAuthenticator;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.labnoratory.sample_app.ViewModelUtil.handleError;

public class AuthenticateViewModel extends ViewModel {

    private static final String TAG = AuthenticateViewModel.class.getSimpleName();

    private final MutableLiveData<String> status = new MutableLiveData<>();

    public MutableLiveData<String> getStatus() {
        return status;
    }

    public void authenticate(Activity activity) {
        new AndroidAuthenticator(activity).authenticate(null).whenComplete((cryptoObject, throwable) -> {
            if (null != throwable) {
                handleError(TAG, status, throwable);
                return;
            }
            status.postValue("Authentication successful");
        });
    }

}
