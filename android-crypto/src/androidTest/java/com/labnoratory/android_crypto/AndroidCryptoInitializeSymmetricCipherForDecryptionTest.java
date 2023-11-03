 package com.labnoratory.android_crypto;

 import org.junit.Test;
 import org.junit.runner.RunWith;

 import androidx.test.ext.junit.runners.AndroidJUnit4;

 import static com.labnoratory.android_crypto.Random.randomBytes;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertThrows;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoInitializeSymmetricCipherForDecryptionTest extends AbstractAndroidCryptoTest {

    @Test
    public void initializeSymmetricCipherForDecryption___key_is_for_asymmetric_encryption___throws_exception() throws Exception {
        String alias = faker.app().name();

        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertThrows(WrongKeyTypeException.class, () -> crypto.initializeSymmetricCipherForDecryption(alias, randomBytes(12)));
    }

    @Test
    public void initializeSymmetricCipherForDecryption___key_is_for_signing___throws_exception() throws Exception {
        String alias = faker.app().name();

        crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertThrows(WrongKeyTypeException.class, () -> crypto.initializeSymmetricCipherForDecryption(alias, randomBytes(12)));
    }

    @Test
    public void initializeSymmetricCipherForDecryption___key_not_found___throws_exception() {
        String alias = faker.app().name();

        assertThrows(KeyNotFoundException.class,() -> crypto.initializeSymmetricCipherForDecryption(alias, randomBytes(12)));
    }

    @Test
    public void initializeSymmetricCipherForDecryption___returns_cipher() throws Exception {
        String alias = faker.app().name();

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        assertNotNull(crypto.initializeSymmetricCipherForDecryption(alias, randomBytes(12)));
    }

}