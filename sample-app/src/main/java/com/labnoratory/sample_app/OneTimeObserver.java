package com.labnoratory.sample_app;

import java.util.function.Consumer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class OneTimeObserver<T> implements Observer<T> {

    private final LiveData<T> liveData;
    private final T initialValue;
    private final Consumer<T> consumer;

    public OneTimeObserver(LiveData<T> liveData, Consumer<T> consumer) {
        this.liveData = liveData;
        this.consumer = consumer;
        this.initialValue = liveData.getValue();
        this.liveData.observeForever(this);
    }

    @Override
    public void onChanged(T t) {
        if(initialValue == null && t==null) return;
        if(initialValue != null && initialValue.equals(t)) return;
        liveData.removeObserver(this);
        consumer.accept(t);
    }

    public static <T> void observeOnce(LiveData<T> liveData, Consumer<T> consumer) {
        new OneTimeObserver<>(liveData, consumer);
    }
}
