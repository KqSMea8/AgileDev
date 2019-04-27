package com.baidu.spark.atest.html;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Element {

	protected final int TIMEOUT = 2;
	protected WebDriver driver;
	protected WebElement we;

	public Element(WebDriver driver, WebElement we) {
		this.driver = driver;
		this.we = we;
	}

	public WebElement getWebElement() {
		return we;
	}

	public boolean isOnTheRightOf(Element another) {

		WebElement left = another.getWebElement();
		WebElement right = we;

		if ((right instanceof RenderedWebElement)
				&& (left instanceof RenderedWebElement)) {
			return (((RenderedWebElement) left).getLocation().x + ((RenderedWebElement) left)
					.getSize().width) <= ((RenderedWebElement) right)
					.getLocation().x;
		}

		return false;
	}

	public WebElement waitForWebElement(WebElement parent, By locator) {
		return waitForWebElement(parent, locator, TIMEOUT);
	}

	public WebElement waitForWebElement(WebElement parent, By locator,
			int timeOutInSeconds) {

		ExpectedCondition<WebElement> condition = new ElementPresent(parent,
				locator);
		Wait<WebDriver> wait = new WebDriverWait(driver, timeOutInSeconds);
		return wait.until(condition);
	}
}
