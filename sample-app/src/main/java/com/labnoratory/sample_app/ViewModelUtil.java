package com.labnoratory.sample_app;

import android.util.Log;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.Optional;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ViewModelUtil {

    public static byte[] getBytes(LiveData<String> payload, String errorMessage) {
        try {
            return Hex.decodeHex(Optional.ofNullable(payload.getValue()).orElse(""));
        } catch (DecoderException e) {
            throw new RuntimeException(errorMessage, e);
        }
    }

    public static void handleError(String logTag, MutableLiveData<String> liveData, Throwable throwable) {
        String msg = throwable.getMessage();
        Log.e(logTag, msg, throwable);
        liveData.postValue(null == msg ? throwable.toString() : msg);
    }
}
