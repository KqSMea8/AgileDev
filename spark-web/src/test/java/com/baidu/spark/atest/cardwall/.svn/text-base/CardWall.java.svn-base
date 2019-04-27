package com.baidu.spark.atest.cardwall;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.baidu.spark.atest.html.Page;

public class CardWall extends Page {
	public final String page_link = "http://localhost:8080/spark-web/spaces/spark/cards/wall#t=product";

	public CardWall(WebDriver driver) {
		this.driver = driver;
	}

	public void open() {
		driver.navigate().to(page_link);
		maxWindows();
		waitForPageToLoad("3000");
	}

	public Card getCard(By by) {
		return new Card(driver, driver.findElement(by));
	}
}
