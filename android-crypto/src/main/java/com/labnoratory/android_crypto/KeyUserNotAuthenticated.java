package com.labnoratory.android_crypto;

public class KeyUserNotAuthenticated extends Exception {

    public KeyUserNotAuthenticated() {
        super("Key requires user authentication but no authenticator passed");
    }
}
