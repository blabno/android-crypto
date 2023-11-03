package com.labnoratory.android_crypto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AccessLevelTest {

    @Test
    public void fromInt() {
        assertEquals(3, AndroidCrypto.AccessLevel.values().length);
        assertEquals(AndroidCrypto.AccessLevel.ALWAYS, AndroidCrypto.AccessLevel.fromInt(0));
        assertEquals(AndroidCrypto.AccessLevel.UNLOCKED_DEVICE, AndroidCrypto.AccessLevel.fromInt(1));
        assertEquals(AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED, AndroidCrypto.AccessLevel.fromInt(2));
    }


}
