package com.labnoratory.android_crypto;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.crypto.SecretKey;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.labnoratory.android_crypto.Random.randomBytes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoDeriveSecretKeyTest extends AbstractAndroidCryptoTest{

    @Test
    public void deriveSecretKey___empty_password_and_empty_salt_and_iterations_is_1___returns_secret_key() {
        SecretKey secretKey = crypto.deriveSecretKey(new byte[]{}, new byte[]{}, 1);
        assertNotNull(secretKey);
        assertEquals("f7ce0b653d2d72a4108cf5abe912ffdd777616dbbb27a70e8204f3ae2d0f6fad", Hex.toHexString(secretKey.getEncoded()));
    }

    @Test
    public void deriveSecretKey___huge_password_and_huge_salt___returns_secret_key() {
        SecretKey secretKey = crypto.deriveSecretKey(randomBytes(9999999), randomBytes(9999999), 9999);
        assertNotNull(secretKey);
    }

    @Test
    public void deriveSecretKey___zero_iterations___throws_exception() {
        IllegalArgumentException exception = assertThrows("abc", IllegalArgumentException.class, () -> crypto.deriveSecretKey(new byte[]{}, new byte[]{}, 0));
        assertEquals("iteration count must be at least 1.", exception.getMessage());
    }

}