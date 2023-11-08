package com.labnoratory.android_crypto;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.labnoratory.android_crypto.Random.randomBytes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoEncryptAsymmetricallyTest extends AbstractAndroidCryptoTest {

    @Test
    public void encryptAsymmetrically___key_is_for_signing___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] bytesToEncrypt = randomBytes(7777);

        crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertThrows(WrongKeyTypeException.class, () -> crypto.encryptAsymmetrically(alias, bytesToEncrypt));
    }

    @Test
    public void encryptAsymmetrically___key_not_found___completes_exceptionally() {
        String alias = faker.app().name();
        byte[] bytesToEncrypt = randomBytes(7777);

        assertThrows(KeyNotFoundException.class, () -> crypto.encryptAsymmetrically(alias, bytesToEncrypt));
    }

    @Test
    public void encryptAsymmetrically___returns_cipherText() throws Exception {
        String alias = faker.app().name();
        byte[] bytesToEncrypt = randomBytes(190);

        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        byte[] cipherText = crypto.encryptAsymmetrically(alias, bytesToEncrypt);
        assertNotNull(cipherText);
        assertNotEquals(0, cipherText.length);
    }

    @Test
    public void encryptAsymmetrically___payload_longer_than_190_bytes___throws_exception() throws Exception {
        String alias = faker.app().name();
        byte[] bytesToEncrypt = randomBytes(191);

        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> crypto.encryptAsymmetrically(alias, bytesToEncrypt));
        assertEquals("Failed to encrypt with asymmetric key", exception.getMessage());
    }
}