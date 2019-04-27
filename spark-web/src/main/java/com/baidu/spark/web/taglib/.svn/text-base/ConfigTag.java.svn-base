package com.baidu.spark.web.taglib;

import java.io.IOException;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import com.baidu.spark.util.MessageHolder;
import com.baidu.spark.util.SparkConfig;

public class ConfigTag extends BodyTagSupport{
	
	private static final long serialVersionUID = 7561173020200907394L;
	private static final String QUOTE = "\"";
	
	protected String key;
	
	
	public int doEndTag() throws JspException {
		
		// 读取config的值
		String value = SparkConfig.getSparkConfig( key );
		
		// 渲染
		JspWriter out = pageContext.getOut();
		try {
			out.print( value );
		} catch (IOException ex) {
			throw new JspException(ex);
		}
		
		return super.doEndTag();
	}


	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}
}
