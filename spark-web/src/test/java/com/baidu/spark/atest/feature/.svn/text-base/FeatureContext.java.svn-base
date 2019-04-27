package com.baidu.spark.atest.feature;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class FeatureContext {
	private WebDriver driver;

	public WebDriver getWebDriver() {
		if (driver == null) {
			FirefoxProfile profile = new FirefoxProfile();
			profile.setPreference("intl.accept_languages", "zh-cn");
			driver = new FirefoxDriver(profile);
		}

		return driver;
	}

}
