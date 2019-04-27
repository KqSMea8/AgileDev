package com.baidu.spark.atest.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class User {
	protected WebDriver driver;

	public void loginIfNeed() {
		driver.get("http://localhost:8080/spark-web/");

		WebElement element = driver.findElement(By.id("username"));
		if (element != null) {
			element.sendKeys(name());
			element = driver.findElement(By.id("password"));
			element.sendKeys(passwd());
			element.submit();
		}
	}

	public abstract String name();

	public abstract String passwd();
}
