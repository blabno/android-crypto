package com.labnoratory.android_crypto;

import com.github.javafaker.Faker;

import org.junit.Before;

import java.security.Key;
import java.security.KeyStore;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.Function;

import static com.labnoratory.android_crypto.Random.getUnique;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public abstract class AbstractAndroidCryptoTest {

    protected AndroidCrypto crypto;
    protected Faker faker;

    @Before
    public void setUp() throws Exception {
        faker = Faker.instance();
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        for (String alias : Collections.list(keyStore.aliases())) {
            keyStore.deleteEntry(alias);
        }
        crypto = new AndroidCrypto();
    }

    protected <T extends Key> void createRemoveKeyScenario(Function<String, T> createKey) throws Exception {
        LinkedList<String> aliases = getUnique(2, () -> faker.app().name());
        String alias1 = aliases.pop();
        String alias2 = aliases.pop();

        assertFalse(crypto.containsKey(alias1));
        assertFalse(crypto.containsKey(alias2));

        assertNotNull(createKey.apply(alias1));
        assertNotNull(createKey.apply(alias2));

        assertTrue(crypto.containsKey(alias1));
        assertTrue(crypto.containsKey(alias2));

        crypto.deleteKey(alias1);

        assertFalse(crypto.containsKey(alias1));
        assertTrue(crypto.containsKey(alias2));
    }
}