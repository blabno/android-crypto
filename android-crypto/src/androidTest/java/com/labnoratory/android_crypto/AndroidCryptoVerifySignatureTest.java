package com.labnoratory.android_crypto;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.PublicKey;
import java.security.Signature;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.labnoratory.android_crypto.Random.randomBytes;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoVerifySignatureTest extends AbstractAndroidCryptoTest {

    @Test
    public void verifySignature___key_is_for_asymmetric_encryption___throws_exception() throws Exception {
        String alias = faker.app().name();
        byte[] content = randomBytes(10);
        byte[] signature = randomBytes(10);

         crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertThrows(WrongKeyTypeException.class, () -> crypto.verifySignature(alias, content, signature));
    }

    @Test
    public void verifySignature___key_is_for_symmetric_encryption___throws_exception() throws Exception {
        String alias = faker.app().name();
        byte[] content = randomBytes(10);
        byte[] signature = randomBytes(10);

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertThrows(KeyNotFoundException.class, () -> crypto.verifySignature(alias, content, signature));
    }

    @Test
    public void verifySignature___key_not_found___throws_exception() {
        String alias = faker.app().name();
        byte[] content = randomBytes(10);
        byte[] signature = randomBytes(10);

        assertThrows(KeyNotFoundException.class,() -> crypto.verifySignature(alias, content,signature));
    }

    @Test
    public void verifySignature___wrong_signature___returns_false() throws Exception {
        String alias = faker.app().name();
        byte[] content = randomBytes(10);

        PublicKey publicKey = crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        Signature signer = crypto.initializeSignatureForSigning(alias);
        byte[] signature = crypto.sign(content, signer);
        byte[] wrongSignature = new byte[signature.length];
        wrongSignature[0]++;

        assertFalse(crypto.verifySignature(alias, content, wrongSignature));
        assertFalse(crypto.verifySignature(publicKey, content, wrongSignature));
    }

    @Test
    public void verifySignature___correct_signature___returns_true() throws Exception {
        String alias = faker.app().name();
        byte[] content = randomBytes(10);

        PublicKey publicKey = crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        Signature signer = crypto.initializeSignatureForSigning(alias);
        byte[] signature = crypto.sign(content, signer);

        assertTrue(crypto.verifySignature(alias, content, signature));
        assertTrue(crypto.verifySignature(publicKey, content, signature));
    }

}