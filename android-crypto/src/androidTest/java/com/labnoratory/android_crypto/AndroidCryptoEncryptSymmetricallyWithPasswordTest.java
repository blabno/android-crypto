package com.labnoratory.android_crypto;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.labnoratory.android_crypto.Random.randomBytes;
import static com.labnoratory.android_crypto.Random.randomInt;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoEncryptSymmetricallyWithPasswordTest extends AbstractAndroidCryptoTest {

    @Test
    public void encryptSymmetricallyWithPassword___returns_cipherText_and_iv() throws Exception {
        String alias = faker.app().name();
        byte[] bytesToEncrypt = randomBytes(7777);
        byte[] password = randomBytes(100);
        byte[] salt = randomBytes(100);
        int iterations = randomInt(2000);

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        EncryptionResult encryptionResult = crypto.encryptSymmetricallyWithPassword(password, salt, iterations, bytesToEncrypt);
        assertNotNull(encryptionResult);
        byte[] cipherText = encryptionResult.getCipherText();
        byte[] iv = encryptionResult.getInitializationVector();
        assertNotNull(cipherText);
        assertNotEquals(0, cipherText.length);
        assertNotNull(iv);
        assertNotEquals(0, iv.length);
    }

}