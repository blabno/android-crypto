package com.labnoratory.android_crypto;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.Signature;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.labnoratory.android_crypto.Random.randomBytes;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoInitializeSignatureForSigningTest extends AbstractAndroidCryptoTest {

    @Test
    public void initializeSignatureForSigning___key_is_for_asymmetric_encryption___throws_exception() throws Exception {
        String alias = faker.app().name();

        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertThrows(WrongKeyTypeException.class, () -> crypto.initializeSignatureForSigning(alias));
    }

    @Test
    public void initializeSignatureForSigning___key_is_for_symmetric_encryption___throws_exception() throws Exception {
        String alias = faker.app().name();

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertThrows(WrongKeyTypeException.class, () -> crypto.initializeSignatureForSigning(alias));
    }

    @Test
    public void initializeSignatureForSigning___key_not_found___throws_exception() {
        String alias = faker.app().name();

        assertThrows(KeyNotFoundException.class,() -> crypto.initializeSignatureForSigning(alias));
    }

    @Test
    public void initializeSignatureForSigning___returns_signature() throws Exception {
        String alias = faker.app().name();
        byte[] input = randomBytes(77777);

        crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        Signature signer = crypto.initializeSignatureForSigning(alias);
        assertNotNull(signer);
        byte[] signature = crypto.sign(input, signer);


        assertTrue(crypto.verifySignature(alias, input, signature));
    }

}