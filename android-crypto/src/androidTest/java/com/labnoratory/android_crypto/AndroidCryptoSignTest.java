package com.labnoratory.android_crypto;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import androidx.biometric.BiometricPrompt;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.labnoratory.android_crypto.E2EUtil.failedFuture;
import static com.labnoratory.android_crypto.Random.randomBytes;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoSignTest extends AbstractAndroidCryptoTest {

    @Test
    public void sign___key_is_for_asymmetric_encryption___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] content = randomBytes(7777);

        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.sign(alias, content).join());
        assertThat(exception.getCause(), is(instanceOf(WrongKeyTypeException.class)));
    }

    @Test
    public void sign___key_is_for_symmetric_encryption___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] content = randomBytes(7777);

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.sign(alias, content).join());
        assertThat(exception.getCause(), is(instanceOf(WrongKeyTypeException.class)));
    }

    @Test
    public void sign___key_not_found___completes_exceptionally() {
        String alias = faker.app().name();
        byte[] content = randomBytes(7777);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.sign(alias, content).join());
        assertThat(exception.getCause(), is(instanceOf(KeyNotFoundException.class)));
    }

    @Test
    public void sign___returns_signature() throws Exception {
        String alias = faker.app().name();
        byte[] content = randomBytes(7777);

        PublicKey publicKey = crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        byte[] signature = crypto.sign(alias, content).join();

        assertTrue(crypto.verifySignature(alias, content, signature));
        assertTrue(crypto.verifySignature(publicKey, content, signature));
    }

    @Test
    public void sign___key_requires_authentication_but_no_authenticator_passed___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] content = randomBytes(7777);

        crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.sign(alias, content).join());
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertThat(cause, is(instanceOf(KeyUserNotAuthenticated.class)));
        assertEquals("Key requires user authentication but no authenticator passed", cause.getMessage());
    }

    @Test
    public void sign___key_requires_authentication_and_authenticator_rejects___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] content = randomBytes(7777);
        RuntimeException rootCause = new RuntimeException("Random exception");
        Authenticator authenticator = mock(Authenticator.class);

        when(authenticator.authenticate(any(BiometricPrompt.CryptoObject.class))).thenReturn(failedFuture(rootCause));

        crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.sign(alias, content, authenticator).join());
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertFailedToDecryptException(cause);
        Throwable causeCause = cause.getCause();
        assertEquals(rootCause, causeCause);
    }

    @Test
    public void sign___key_requires_authentication_and_cipher_throws_during_doFinal___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] content = randomBytes(7777);
        Authenticator authenticator = mock(Authenticator.class);
        Signature signature = mock(Signature.class);

        when(authenticator.authenticate(any(BiometricPrompt.CryptoObject.class))).thenReturn(CompletableFuture.completedFuture(new BiometricPrompt.CryptoObject(signature)));

        crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.sign(alias, content, authenticator).join());
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertFailedToDecryptException(cause);
        Throwable causeCause = cause.getCause();
        assertThat(causeCause, is(instanceOf(SignatureException.class)));
    }

    private static void assertFailedToDecryptException(Throwable ex) {
        assertThat(ex, is(instanceOf(RuntimeException.class)));
        assertEquals("Failed to sign message", ex.getMessage());
    }

}