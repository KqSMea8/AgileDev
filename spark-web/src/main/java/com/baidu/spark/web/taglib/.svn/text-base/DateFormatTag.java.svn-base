package com.baidu.spark.web.taglib;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.baidu.spark.util.MessageHolder;

/**
 * Diff date format tag
 * 
 * @author zhangjing_pe
 * @author GuoLin
 */
public class DateFormatTag extends BodyTagSupport {
	
	private static final long serialVersionUID = -728710799028681003L;
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	/** x秒前的国际化key. */
	private static final String KEY_SECONDS_AGO = "tag.diffDateTag.ago.second";
	
	/** x分钟前的国际化key. */
	private static final String KEY_MINUTES_AGO = "tag.diffDateTag.ago.minute";
	
	/** 今天的时间国际化key. */
	private static final String KEY_TODAY = "tag.diffDateTag.ago.today";
	
	/** 昨天的时间国际化key. */
	private static final String KEY_YESTERDAY = "tag.diffDateTag.ago.yesterday";
	
	/** 日期时间格式国际化key. */
	private static final String DEFAULT_DATETIME_FORMAT_KEY = "format.date.long-datetime-minute";
	
	/** 时间格式国际化key. */
	private static final String DEFAULT_TIME_FORMAT_KEY = "format.date.long-time-minute";

	/** 日期对象. */
	private Date value;
	
	/** 正常日期格式的国际化key. */
	private String datetimeFormatKey;
	
	/** 正常时间格式的国际化key. */
	private String timeFormatKey;
	
	@Override
	public int doEndTag() throws JspException {
		
		if (value == null){
			return super.doEndTag();
		}
		
		// 给定默认值
		if (datetimeFormatKey == null) {
			datetimeFormatKey = DEFAULT_DATETIME_FORMAT_KEY;
		}
		if (timeFormatKey == null) {
			timeFormatKey = DEFAULT_TIME_FORMAT_KEY;
		}
		
		// 格式化日期
		String message = getDiffMessage(value, new Date());
		
		// 渲染
		try {
			pageContext.getOut().print(message);
		} catch (IOException e) {
			logger.error("date format error", e);
		}
		
		return super.doEndTag();
	}
	
	/**
	 * 获取两个日期之间的diff比较结果.
	 * @param before 早些的时间
	 * @param after 晚些的时间
	 * @return 格式化完成的时间差异字符串
	 */
	public String getDiffMessage(final Date before, final Date after) {
		Assert.notNull(before);
		
		// 在同一自然天内
		if (DateUtils.isSameDay(before, after)) {
			long diff = after.getTime() - before.getTime();
			
			// 小于一秒钟
			if (diff < DateUtils.MILLIS_PER_SECOND) {
				return MessageHolder.get(KEY_SECONDS_AGO, 1);
			}
			// 小于一分钟
			else if (diff < DateUtils.MILLIS_PER_MINUTE) {
				return MessageHolder.get(KEY_SECONDS_AGO, diff / DateUtils.MILLIS_PER_SECOND);
			}
			// 小于一小时
			else if (diff < DateUtils.MILLIS_PER_HOUR) {
				return MessageHolder.get(KEY_MINUTES_AGO, diff / DateUtils.MILLIS_PER_MINUTE);
			}
			// 否则必然在一个自然天内且大于一小时
			else {
				return MessageHolder.get(KEY_TODAY, format(before, timeFormatKey));
			}
		}
		// 前一个自然天内
		else if (DateUtils.isSameDay(DateUtils.addDays(before, 1), after)) {
			return MessageHolder.get(KEY_YESTERDAY, format(before, timeFormatKey));
		}
		// 超过两个自然天
		else {
			return format(before, datetimeFormatKey);
		}
	}
	
	/**
	 * 按国际化配置格式化日期.
	 * @param date 日期
	 * @param patternKey 格式的国际化消息key
	 * @return 格式化后的日期字符串
	 */
	private static String format(Date date, String patternKey) {
		String pattern = MessageHolder.get(patternKey);
		return DateFormatUtils.format(date, pattern);
	}

	public void setValue(Date value) {
		this.value = value;
	}

	public void setDatetimeFormatKey(String datetimeFormatKey) {
		this.datetimeFormatKey = datetimeFormatKey;
	}

	public void setTimeFormatKey(String timeFormatKey) {
		this.timeFormatKey = timeFormatKey;
	}

}
