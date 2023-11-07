package com.labnoratory.android_device.e2e;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;

public class SymmetricEncryptionFragment {

    private final AndroidDriver driver;

    public static WebElement getCipherTextElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("cipherText"));
    }

    public static WebElement getCreateKeyButton(WebDriver driver) {
        return driver.findElement(AppiumBy.id("createKeyButton"));
    }

    public static WebElement getDecryptButton(WebDriver driver) {
        return driver.findElement(AppiumBy.id("decryptButton"));
    }

    public static WebElement getEncryptButton(WebDriver driver) {
        return driver.findElement(AppiumBy.id("encryptButton"));
    }

    public static WebElement getInputElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("input"));
    }

    public static WebElement getIvElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("iv"));
    }

    public static WebElement getStatusElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("status"));
    }

    public SymmetricEncryptionFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public SymmetricEncryptionFragment assertStatus(String pattern) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(1))
                    .until(webDriver -> {
                        try {
                            return getStatusElement(webDriver).getText().matches(pattern);
                        } catch (Exception ignore) {
                            return false;
                        }
                    });
        } catch (TimeoutException ignore) {
            assertThat(getStatusElement(driver).getText(), matchesPattern(pattern));
        }
        return this;
    }

    public SymmetricEncryptionFragment setInput(CharSequence... keysToSend) {
        WebElement element = getInputElement(driver);
        element.clear();
        element.sendKeys(keysToSend);
        return this;
    }

    /** @noinspection UnusedReturnValue*/
    public SymmetricEncryptionFragment clickCreateKeyButton() {
        getCreateKeyButton(driver).click();
        return this;
    }

    public SymmetricEncryptionFragment clickDecryptButton() {
        getDecryptButton(driver).click();
        return this;
    }

    public SymmetricEncryptionFragment clickEncryptButton() {
        getEncryptButton(driver).click();
        return this;
    }

    public SymmetricEncryptionFragment createKey() {
        clickCreateKeyButton();
        assertStatus("Encryption key created successfully");
        return this;
    }

    public String getCipherText() {
        return getCipherTextElement(driver).getText();
    }

    public String getIv() {
        return getIvElement(driver).getText();
    }
}
