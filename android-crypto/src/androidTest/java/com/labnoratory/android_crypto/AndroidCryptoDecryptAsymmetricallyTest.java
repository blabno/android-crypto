package com.labnoratory.android_crypto;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.crypto.Cipher;

import androidx.biometric.BiometricPrompt;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.labnoratory.android_crypto.E2EUtil.failedFuture;
import static com.labnoratory.android_crypto.Random.getUnique;
import static com.labnoratory.android_crypto.Random.randomBytes;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoDecryptAsymmetricallyTest extends AbstractAndroidCryptoTest {

    private static final String VALID_CIPHER_TEXT = "9ce1d10e731e6260042520b07599c497afb0223f9ef3d4735867c56366f5f3e11f3f88779197b1396c48ad05e101525a6fa51ed012c7b9bf88cca14982849e1b406199465641cd751c9a1793b1f300d72bc1f620ff21926c0bc5837f6238749139fc515c0ec1c62ae6a456e528502311e27cec432492dc6e15bd1285c19d6e580bfc71844e277d1fb40205810cb7303b071a2693be35a963665147826750a6cd085bf082cbcbdeb354910f94f2f6efb2231b60055fb9d45ea49d549f21fe178b989b6557aef88b27bfec6095bbbdfee666bd391f0b8e271f0edcee16439dc1e8633971bb1bb72601042e3ba47ea19f30c72689aac90ea578f1028e11713f2b28";

    @Test
    public void decryptAsymmetrically___key_is_for_signing___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] cipherText = Hex.decode(VALID_CIPHER_TEXT);

        crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.decryptAsymmetrically(alias, cipherText).join());
        assertThat(exception.getCause(),is(instanceOf(WrongKeyTypeException.class)));
    }

    @Test
    public void decryptAsymmetrically___key_not_found___completes_exceptionally() {
        String alias = faker.app().name();
        byte[] cipherText = Hex.decode(VALID_CIPHER_TEXT);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.decryptAsymmetrically(alias, cipherText).join());
        assertThat(exception.getCause(),is(instanceOf(KeyNotFoundException.class)));
    }

    @Test
    public void decryptAsymmetrically___encrypted_with_same_key___completes_with_decrypted_bytes() throws Exception {
        String alias = faker.app().name();

        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        byte[] bytesToEncrypt = randomBytes(190);
        byte[] cipherText = crypto.encryptAsymmetrically(alias, bytesToEncrypt);
        byte[] decryptedBytes = crypto.decryptAsymmetrically(alias, cipherText).join();
        assertArrayEquals(bytesToEncrypt, decryptedBytes);
    }

    @Test
    public void decryptAsymmetrically___encrypted_with_different_key___completes_exceptionally() throws Exception {
        LinkedList<String> aliases = getUnique(2, () -> faker.app().name());
        String alias1 = aliases.pop();
        String alias2 = aliases.pop();

        crypto.createAsymmetricEncryptionKey(alias1, AndroidCrypto.AccessLevel.ALWAYS, false);
        crypto.createAsymmetricEncryptionKey(alias2, AndroidCrypto.AccessLevel.ALWAYS, false);
        byte[] bytesToEncrypt = randomBytes(190);
        byte[] cipherText = crypto.encryptAsymmetrically(alias1, bytesToEncrypt);
        RuntimeException exception = assertThrows(CompletionException.class, () -> crypto.decryptAsymmetrically(alias2, cipherText).join());
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertFailedToDecryptException(cause);
        assertNotNull(cause.getCause());
    }

    @Test
    public void decryptAsymmetrically___key_requires_authentication_but_no_authenticator_passed___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] cipherText = Hex.decode(VALID_CIPHER_TEXT);

        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.decryptAsymmetrically(alias, cipherText).join());
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertThat(cause, is(instanceOf(KeyUserNotAuthenticated.class)));
        assertEquals("Key requires user authentication but no authenticator passed", cause.getMessage());
    }

    @Test
    public void decryptAsymmetrically___key_requires_authentication_and_authenticator_rejects___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] cipherText = Hex.decode(VALID_CIPHER_TEXT);
        RuntimeException rootCause = new RuntimeException("Random exception");
        Authenticator authenticator = mock(Authenticator.class);

        when(authenticator.authenticate(any(BiometricPrompt.CryptoObject.class))).thenReturn(failedFuture(rootCause));

        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.decryptAsymmetrically(alias, cipherText, authenticator).join());
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertFailedToDecryptException(cause);
        Throwable causeCause = cause.getCause();
        assertEquals(rootCause,causeCause);
    }

    @Test
    public void encryptSymmetrically___key_requires_authentication_and_cipher_throws_during_doFinal___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] cipherText = Hex.decode(VALID_CIPHER_TEXT);
        Authenticator authenticator = mock(Authenticator.class);
        Cipher cipher = mock(Cipher.class);

        when(authenticator.authenticate(any(BiometricPrompt.CryptoObject.class))).thenReturn(CompletableFuture.completedFuture(new BiometricPrompt.CryptoObject(cipher)));

        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.decryptAsymmetrically(alias, cipherText, authenticator).join());
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertFailedToDecryptException(cause);
        Throwable causeCause = cause.getCause();
        assertThat(causeCause, is(instanceOf(IllegalStateException.class)));
    }

    private static void assertFailedToDecryptException(Throwable ex) {
        assertThat(ex, is(instanceOf(RuntimeException.class)));
        assertEquals("Failed to decrypt with asymmetric key", ex.getMessage());
    }

}