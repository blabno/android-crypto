package com.labnoratory.android_crypto;

import org.junit.Test;

import java.util.Collections;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class OptionsTest {

    @Test
    public void getString___key_is_null___returns_default_value() {
        String key = "a";
        String defaultValue = "b";
        String value = Options.getString(Collections.singletonMap(key, null), key, defaultValue);

        assertEquals(defaultValue, value);
    }

    @Test
    public void getString___key_missing___returns_default_value() {
        String key = "a";
        String defaultValue = "b";
        String value = Options.getString(Collections.emptyMap(), key, defaultValue);

        assertEquals(defaultValue, value);
    }

    @Test
    public void getString___value_is_number___returns_value() {
        String key = "a";
        int optionValue = new Random().nextInt();
        String defaultValue = String.format("not-%d", optionValue);
        String value = Options.getString(Collections.singletonMap(key, optionValue), key, defaultValue);

        assertEquals(String.format("%d", optionValue), value);
    }

    @Test
    public void getString___value_is_string___returns_value() {
        String key = "a";
        String optionValue = String.format("%d", new Random().nextInt());
        String defaultValue = String.format("not-%s", optionValue);
        String value = Options.getString(Collections.singletonMap(key, optionValue), key, defaultValue);

        assertEquals(optionValue, value);
    }
}
