package com.labnoratory.android_device.e2e;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.net.URL;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class TestSuiteSetup {

    @BeforeSuite
    public static void beforeClass() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options()
                .setApp("./sample-app/build/outputs/apk/debug/sample-app-debug.apk");
        AndroidDriver driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
        AndroidDriverFactory.setInstance(driver);
        SecuritySettingsFragment.setupFingerprint(driver);
    }

    @AfterSuite
    public static void afterClass() {
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        if (null != driver)
            driver.quit();
    }
}
