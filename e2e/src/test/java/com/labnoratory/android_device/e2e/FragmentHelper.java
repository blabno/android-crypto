package com.labnoratory.android_device.e2e;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;

public class FragmentHelper {

    public static void assertText(WebDriver driver, Function<WebDriver, WebElement> getElement, String pattern) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(1))
                    .until(webDriver -> {
                        try {
                            return getElement.apply(webDriver).getText().matches(pattern);
                        } catch (Exception ignore) {
                            return false;
                        }
                    });
        } catch (TimeoutException ignore) {
            assertThat(getElement.apply(driver).getText(), matchesPattern(pattern));
        }
    }
}