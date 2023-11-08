package com.labnoratory.android_crypto;

import java.util.concurrent.CompletableFuture;

public class E2EUtil {

    public static <T> CompletableFuture<T> failedFuture(Throwable throwable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(throwable);
        return future;
    }

}
