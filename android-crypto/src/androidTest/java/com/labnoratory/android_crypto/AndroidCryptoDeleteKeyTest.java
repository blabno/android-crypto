package com.labnoratory.android_crypto;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoDeleteKeyTest extends AbstractAndroidCryptoTest {

    @Test
    public void deleteKey___keyNotFound___no_exception() throws Exception {
        String alias = faker.app().name();

        assertFalse(crypto.containsKey(alias));

        crypto.deleteKey(alias);
    }
}