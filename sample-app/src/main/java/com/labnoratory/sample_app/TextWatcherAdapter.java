package com.labnoratory.sample_app;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.lifecycle.MutableLiveData;

public class TextWatcherAdapter implements TextWatcher {

    private final MutableLiveData<String> liveData;

    public TextWatcherAdapter(MutableLiveData<String> liveData) {
        this.liveData = liveData;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String value = s.toString();
        if (!value.equals(liveData.getValue())) {
            liveData.postValue(value);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
