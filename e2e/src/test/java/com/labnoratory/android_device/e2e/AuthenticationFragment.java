package com.labnoratory.android_device.e2e;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.FragmentHelper.assertText;

public class AuthenticationFragment {

    private final AndroidDriver driver;

    public static WebElement getAuthenticateButton(WebDriver driver) {
        return driver.findElement(AppiumBy.id("authenticateButton"));
    }

    public static WebElement getStatusElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("status"));
    }

    public AuthenticationFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public AuthenticationFragment assertStatus(String pattern) {
        assertText(driver, AuthenticationFragment::getStatusElement, pattern);
        return this;
    }

    /** @noinspection UnusedReturnValue*/
    public AuthenticationFragment clickAuthenticateButton() {
        getAuthenticateButton(driver).click();
        return this;
    }
}
