package com.baidu.spark.atest.common;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Utils {

	private static WebElement _element;

	public static WebElement waitForElement(WebDriver driver,
			final WebElement parent, final By by, int timeOutInSeconds) {
		WebElement result = null;
		ExpectedCondition<Boolean> e = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				if (parent == null) {
					_element = d.findElement(by);
				} else {
					_element = parent.findElement(by);
				}
				return Boolean.TRUE;
			}
		};

		Wait<WebDriver> wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(e);

		if (_element != null) {
			result = _element;
			_element = null;
		}

		return result;
	}
}
