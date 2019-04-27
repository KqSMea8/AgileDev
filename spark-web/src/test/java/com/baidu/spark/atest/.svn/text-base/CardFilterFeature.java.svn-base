package com.baidu.spark.atest;

import static org.junit.Assert.assertTrue;

import com.baidu.spark.atest.feature.Feature;
import com.baidu.spark.atest.feature.FeatureContext;
import com.baidu.spark.atest.list.List;
import com.baidu.spark.atest.user.TianYuSong;

import cuke4duke.annotation.I18n.ZH_CN.当;
import cuke4duke.annotation.I18n.ZH_CN.而且;
import cuke4duke.annotation.I18n.ZH_CN.那么;

/**
 * 卡片过滤.feature
 * 
 */
public class CardFilterFeature extends Feature {
	private TianYuSong tianyusong;
	private List list;

	public CardFilterFeature(FeatureContext context) {
		this.context = context;
		tianyusong = new TianYuSong(context.getWebDriver());
		list = new List(context.getWebDriver());
	}

	@当("^打开列表视图$")
	public void openList() {
		list.open();
	}

	@而且("^输入创建人等于中文名$")
	public void inputChineseName() {
		list.getFilterPanel().inputQuery("创建人", "中文名");
		list.getFilterPanel().clickQueryButton();
	}

	@那么("^显示当前没有符合条件的卡片$")
	public void showErrorMessage() {
		assertTrue(list.isErrorMessagePresent());

		context.getWebDriver().close();
	}
}
