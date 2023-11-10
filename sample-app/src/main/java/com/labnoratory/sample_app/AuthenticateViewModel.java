package com.labnoratory.sample_app;

import android.app.Activity;
import android.util.Log;

import com.labnoratory.android_crypto.AndroidAuthenticator;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthenticateViewModel extends ViewModel {

    private static final String TAG = AuthenticateViewModel.class.getSimpleName();

    private final MutableLiveData<String> status = new MutableLiveData<>();

    public MutableLiveData<String> getStatus() {
        return status;
    }

    public void authenticate(Activity activity) {
        new AndroidAuthenticator(activity).authenticate(null).whenComplete((cryptoObject, throwable) -> {
            if (null != throwable) {
                String msg = throwable.getMessage();
                Log.e(TAG, msg, throwable);
                status.postValue("Authentication failed");
                return;
            }
            status.postValue("Authentication successful");
        });
    }

    public void clear() {
        status.postValue("");
    }

}
