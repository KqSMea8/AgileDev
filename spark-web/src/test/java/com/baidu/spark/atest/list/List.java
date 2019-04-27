package com.baidu.spark.atest.list;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.baidu.spark.atest.common.FilterPanel;
import com.baidu.spark.atest.html.ElementPresent;
import com.baidu.spark.atest.html.Page;

public class List extends Page {

	public final String page_link = "http://localhost:8080/spark-web/spaces/spark/cards/list#t=product";

	public List(WebDriver driver) {
		this.driver = driver;
	}

	public void open() {
		driver.navigate().to(page_link);
		maxWindows();
		waitForPageToLoad("3000");
	}

	public FilterPanel getFilterPanel() {
		return new FilterPanel(driver, driver.findElement(By
				.className("filter")));
	}

	public boolean isErrorMessagePresent() {
		ExpectedCondition<WebElement> condition = new ElementPresent(null,
				By.id("error-message"));
		Wait<WebDriver> wait = new WebDriverWait(driver, 10);
		WebElement errorMsg = wait.until(condition);

		if (errorMsg != null) {
			return true;
		}

		return false;
	}
}
