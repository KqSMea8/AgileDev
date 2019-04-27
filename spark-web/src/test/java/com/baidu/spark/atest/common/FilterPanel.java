package com.baidu.spark.atest.common;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.baidu.spark.atest.html.Element;

public class FilterPanel extends Element {
	public FilterPanel(WebDriver driver, WebElement we) {
		super(driver, we);
	}

	public void inputQuery(String key, String value) {
		WebElement queryDiv = waitForWebElement(we, By.id("queryDiv"));
		WebElement linkQuery = queryDiv.findElement(By.linkText("(请选择...)"));
		linkQuery.click();

		WebElement linkMenuQuery = waitForWebElement(queryDiv, By.linkText(key));
		linkMenuQuery.click();

		WebElement queryRight = queryDiv.findElement(By
				.className("query-right"));

		WebElement linkInputQuery = waitForWebElement(queryRight,
				By.tagName("a"));
		linkInputQuery.click();

		WebElement inputQuery = waitForWebElement(queryRight,
				By.className("ac_input"));
		inputQuery.sendKeys(value);
	}

	public void clickQueryButton() {
		WebElement queryButtonContainerDiv = waitForWebElement(we,
				By.className("query-button-container"));
		queryButtonContainerDiv.findElement(By.className("search-button"))
				.click();
	}
}
