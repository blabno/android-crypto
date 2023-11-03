package com.labnoratory.android_crypto;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoGetPublicKeyTest extends AbstractAndroidCryptoTest{

    @Test
    public void getPublicKey___key_not_found___throws_exception() throws Exception {
        String alias = faker.app().name();

        assertFalse(crypto.containsKey(alias));

        assertThrows(KeyNotFoundException.class, () -> crypto.getPublicKey(alias));
    }

    @Test
    public void getPublicKey___asymmetric_encryption_key___returns_public_key() throws Exception {
        String alias = faker.app().name();

        assertThrows(KeyNotFoundException.class, () -> crypto.getPublicKey(alias));
        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertNotNull(crypto.getPublicKey(alias));
    }

    @Test
    public void getPublicKey___signing_key___returns_public_key() throws Exception {
        String alias = faker.app().name();

        assertThrows(KeyNotFoundException.class, () -> crypto.getPublicKey(alias));
        crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertNotNull(crypto.getPublicKey(alias));
    }

    @Test
    public void getPublicKey___symmetric_encryption_key___throws_exception() throws Exception {
        String alias = faker.app().name();

        assertThrows(KeyNotFoundException.class, () -> crypto.getPublicKey(alias));
        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertThrows(KeyNotFoundException.class, () -> crypto.getPublicKey(alias));
    }
}