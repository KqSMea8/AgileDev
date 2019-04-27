package com.baidu.spark.atest.hierarchy;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.baidu.spark.atest.html.Element;

public class ListItem extends Element {

	private WebElement we;
	private final String SUBS_LINK = ".//div/div[2]/a";

	public ListItem(WebDriver driver, WebElement we) {
		super(driver, we);
	}

	public WebElement getSubsLink() {
		return we.findElement(By.xpath(SUBS_LINK));
	}

	public void expendSubElement() {
		getSubsLink().click();
	}

	public boolean hasSubCard() {
		return getSubsLink() != null;
	}

	public boolean isSubCardExpended() {
		WebElement subs_link = getSubsLink();
		if (subs_link == null)
			return false;
		return subs_link.getAttribute("class").equalsIgnoreCase("collapse");
	}

}
