package com.labnoratory.android_device.e2e;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.net.URL;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class AbstractE2ETest {

    protected static AndroidDriver driver;

    @BeforeClass
    public static void beforeClass() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options()
                .setApp("./sample-app/build/outputs/apk/debug/sample-app-debug.apk");
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
        SecuritySettingsFragment.setupFingerprint(driver);
    }

    @AfterClass
    public static void afterClass() {
        if (null != driver) driver.quit();
    }

}