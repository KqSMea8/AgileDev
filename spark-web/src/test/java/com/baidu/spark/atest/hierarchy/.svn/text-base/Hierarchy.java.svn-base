package com.baidu.spark.atest.hierarchy;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import com.baidu.spark.atest.html.Page;

public class Hierarchy extends Page {
	public final String page_link = "http://localhost:8080/spark-web/spaces/spark/cards/hierarchy#t=product";
	private final String COOKIE_OPEN = "open";

	public Hierarchy(WebDriver driver) {
		this.driver = driver;
	}

	public void open() {
		driver.navigate().to(page_link);
		maxWindows();
		waitForPageToLoad("3000");
		waitForNothing(3000);
	}

	public void openWithAllCardClose() {
		driver.manage().deleteCookieNamed(COOKIE_OPEN);
		open();
	}

	public ListItem getListItem(By by) {
		return new ListItem(driver, driver.findElement(by));
	}

	public String getOpenValueInCookie() {
		return driver.manage().getCookieNamed(COOKIE_OPEN).getValue();
	}

	public void setOpenValueInCookie() {
		driver.manage().deleteCookieNamed(COOKIE_OPEN);
		Cookie cookie = new Cookie(COOKIE_OPEN, "200");
		driver.manage().addCookie(cookie);
	}

}
