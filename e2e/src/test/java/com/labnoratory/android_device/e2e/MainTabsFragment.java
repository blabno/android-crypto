package com.labnoratory.android_device.e2e;

import org.openqa.selenium.WebElement;

import java.util.List;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class MainTabsFragment {

    private final AndroidDriver driver;

    public MainTabsFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public List<WebElement> getTabs() {
        return driver.findElements(AppiumBy.xpath("//*[contains(@resource-id,\":id/tabLayout\")]//android.widget.TextView"));
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public MainTabsFragment clickAsymmetricEncryption() {
        getTabs().get(2).click();
        new AsymmetricEncryptionFragment(driver).waitUntilDisplayed();
        return this;
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public MainTabsFragment clickSymmetricEncryption() {
        getTabs().get(1).click();
        new SymmetricEncryptionFragment(driver).waitUntilDisplayed();
        return this;
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public MainTabsFragment clickSymmetricEncryptionWithPassword() {
        getTabs().get(3).click();
        new SymmetricEncryptionWithPasswordFragment(driver).waitUntilDisplayed();
        return this;
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public MainTabsFragment clickAuthentication() {
        getTabs().get(0).click();
        new AuthenticationFragment(driver).waitUntilDisplayed();
        return this;
    }
}
