package com.baidu.spark.atest.html;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.seleniumemulation.CompoundMutator;
import org.openqa.selenium.internal.seleniumemulation.WaitForCondition;
import org.openqa.selenium.internal.seleniumemulation.WaitForPageToLoad;
import org.openqa.selenium.remote.RenderedRemoteWebElement;

public class Page {
	protected WebDriver driver;
	protected WaitForPageToLoad waitPage = new WaitForPageToLoad();
	protected WaitForCondition waitCondition = new WaitForCondition(
			new CompoundMutator("http://localhost:8080/spark-web/"));

	public void waitForNothing(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

	public void waitForPageToLoad(String time) {
		waitPage.apply(driver, new String[] { time });
	}

	public void waitForCondition(String script, String time) {
		waitCondition.apply(driver, new String[] { script, time });
	}

	public static Element waitForWebElement(ElementRetriever retriever,
			int second) {
		Element element = null;
		int count = 0;
		while (count < second) {
			try {
				element = retriever.retrieve();
			} catch (NoSuchElementException e) {
				count++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
				}
			}
			if (element != null) {
				break;
			}
		}

		return element;
	}

	public void maxWindows() {
		// WindowMaximize wm = new WindowMaximize(new JavascriptLibrary());
		// wm.apply(driver, new String[] {});
		((JavascriptExecutor) driver)
				.executeScript("window.resizeTo(1024, 768);");
		// new WebDriverBackedSelenium(driver,
		// "http://localhost:8080/spark-web/")
		// .windowMaximize();
	}

	public boolean isOnTheRightOf(WebElement left, WebElement right) {
		if ((right instanceof RenderedRemoteWebElement)
				&& (left instanceof RenderedRemoteWebElement)) {
			return (((RenderedRemoteWebElement) left).getLocation().x + ((RenderedRemoteWebElement) left)
					.getSize().width) < ((RenderedRemoteWebElement) right)
					.getLocation().x;
		}

		return false;
	}
}
