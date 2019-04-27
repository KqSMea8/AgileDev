package com.baidu.spark.atest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.baidu.spark.atest.feature.Feature;
import com.baidu.spark.atest.hierarchy.Hierarchy;
import com.baidu.spark.atest.user.TianYuSong;

import cuke4duke.annotation.I18n.ZH_CN.假如;
import cuke4duke.annotation.I18n.ZH_CN.当;
import cuke4duke.annotation.I18n.ZH_CN.而且;
import cuke4duke.annotation.I18n.ZH_CN.那么;

public class HierarchyStatusFeature extends Feature {
	private WebDriver driver = new FirefoxDriver();
	private TianYuSong tianyusong = new TianYuSong(driver);
	private Hierarchy hierarchy = new Hierarchy(driver);

	@假如("^用户([A-z]*)访问层级视图$")
	public void theUserIs(String user) {
		tianyusong.loginIfNeed();
	}

	@当("^打开所有层级都收起的层级视图$")
	public void openHierarchyWithAllCardClose() {
		hierarchy.openWithAllCardClose();
	}

	@而且("^展开一个子卡片$")
	public void expandSubCard() {
		hierarchy.getListItem(By.id("card-201")).expendSubElement();
	}

	@那么("^cookie记录状态$")
	public void checkCookieStatus() {
		String cookie_open_str = hierarchy.getOpenValueInCookie();
		assertEquals("201", cookie_open_str);
		
		driver.close();
	}

	@当("^设置cookie记录$")
	public void setCookieOpenValue() {
		hierarchy.setOpenValueInCookie();
	}

	@而且("^打开层级试图$")
	public void openHierarchy() {
		hierarchy.open();
	}

	@那么("^展开相应子卡片$")
	public void checkSubCardStatus() {
		assertTrue(hierarchy.getListItem(By.id("card-200")).isSubCardExpended());
		assertFalse(hierarchy.getListItem(By.id("card-201")).isSubCardExpended());
		
		driver.close();
	}
}
