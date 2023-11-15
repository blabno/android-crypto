package com.labnoratory.android_device.e2e;

import org.testng.annotations.Test;

import io.appium.java_client.android.AndroidDriver;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class AuthenticationE2ETest {

    @Test
    public void authenticate___authentication_succeeds() {
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        new MainTabsFragment(driver)
                .clickAuthentication()
                .waitUntilDisplayed()
                .clickClearButton()
                .clickAuthenticateButton()
                .scanUnknownFinger()
                .scanEnrolledFinger()
                .assertStatus(is(equalTo("Authentication successful")));
    }

    @Test
    public void authenticate___authentication_cancelled() {
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        new MainTabsFragment(driver)
                .clickAuthentication()
                .waitUntilDisplayed()
                .clickClearButton()
                .clickAuthenticateButton()
                .cancelBiometricAuthentication()
                .assertStatus(is(equalTo("Authentication failed")));
    }

}