package com.baidu.spark.atest.html;

import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class ElementPresent implements ExpectedCondition<WebElement> {
	private WebElement parent;
	private By locator;

	public ElementPresent(WebElement parent, By locator) {
		this.parent = parent;
		this.locator = locator;
	}

	@Override
	public WebElement apply(WebDriver driver) {
		WebElement element;
		if (parent != null) {
			element = parent.findElement(locator);
		} else {
			element = driver.findElement(locator);
		}
		if (!((RenderedWebElement) element).isDisplayed()) {
			throw new NotFoundException("Element not displayed");
		}
		return element;
	}
}
