package com.labnoratory.android_device.e2e;

import org.apache.commons.codec.binary.Hex;

public class Random {

    private static final java.util.Random random = new java.util.Random();

    public static byte[] randomBytes(int length) {
        byte[] result = new byte[length];
        random.nextBytes(result);
        return result;
    }

    public static String randomString() {
        return Hex.encodeHexString(randomBytes(10));
    }
}
