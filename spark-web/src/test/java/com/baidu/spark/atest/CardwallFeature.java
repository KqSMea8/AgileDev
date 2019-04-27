package com.baidu.spark.atest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;

import com.baidu.spark.atest.cardwall.CardWall;
import com.baidu.spark.atest.feature.Feature;
import com.baidu.spark.atest.feature.FeatureContext;
import com.baidu.spark.atest.html.Element;
import com.baidu.spark.atest.html.ElementRetriever;
import com.baidu.spark.atest.user.TianYuSong;

import cuke4duke.annotation.I18n.ZH_CN.假如;
import cuke4duke.annotation.I18n.ZH_CN.当;
import cuke4duke.annotation.I18n.ZH_CN.那么;

public class CardwallFeature extends Feature {

	private TianYuSong tianyusong;
	private CardWall cardWall;

	public CardwallFeature(FeatureContext context) {
		this.context = context;
		tianyusong = new TianYuSong(context.getWebDriver());
		cardWall = new CardWall(context.getWebDriver());
	}

	// @Given("^The User is ([A-z]*)$")
	@假如("^用户是([A-z]*)$")
	public void theUserIs(String user) {
		tianyusong.loginIfNeed();
	}

	// @When("^Open cardwall$")
	@当("^打开卡片墙$")
	public void openCardwall() {
		cardWall.open();
	}

	// @Then("^The cardwall is rendered$")
	@那么("^卡片墙正确显示$")
	public void theCardwalIsRendered() {
		Element card199 = waitForElement(new ElementRetriever() {
			public Element retrieve() {
				return cardWall.getCard(By.id("card-199"));
			}
		}, 10);

		assertNotNull(card199);

		Element card198 = waitForElement(new ElementRetriever() {
			public Element retrieve() {
				return cardWall.getCard(By.id("card-198"));
			}
		}, 10);

		assertNotNull(card198);

		boolean b = card199.isOnTheRightOf(card198);
		assertTrue(b);

		context.getWebDriver().close();
	}
}
