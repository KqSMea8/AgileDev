package com.baidu.spark.atest.feature;

import org.openqa.selenium.NoSuchElementException;

import com.baidu.spark.atest.html.Element;
import com.baidu.spark.atest.html.ElementRetriever;

public class Feature {

	protected FeatureContext context;

	public Element waitForElement(ElementRetriever retriever, int second) {
		Element element = null;
		int count = 0;
		while (count < second) {
			try {
				element = retriever.retrieve();
			} catch (NoSuchElementException e) {
				count++;
				waitForNothing(1000);
			}
			if (element != null) {
				break;
			}
		}

		return element;
	}

	public void waitForNothing(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

}
