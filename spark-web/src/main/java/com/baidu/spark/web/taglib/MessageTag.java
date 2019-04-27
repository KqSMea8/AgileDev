package com.baidu.spark.web.taglib;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.baidu.spark.util.MessageHolder;

/**
 * 国际化标签.
 * <p>
 * 用于在页面上生成一个可以由JavaScript调用的国际化函数.
 * </p>
 * 
 * @author GuoLin
 *
 */
public class MessageTag extends BodyTagSupport {

	private static final long serialVersionUID = 2691529383465390048L;
	
	private static final String QUOTE = "\"";
	
	public int doEndTag() throws JspException {
		
		// 拼装JavaScript Map
		StringBuilder jsCode = new StringBuilder();
		jsCode.append("{");
		Map<String, String> messages = MessageHolder.all();
		for (Map.Entry<String, String> entry : messages.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (value != null) {
				value = value.replace("\"", "\\\"");
			}
			jsCode.append(QUOTE).append(key).append(QUOTE)
					.append(":")
					.append(QUOTE).append(value).append(QUOTE)
					.append(",");
		}
		jsCode.append("\"\":\"\" }");
		
		// 渲染
		JspWriter out = pageContext.getOut();
		try {
			out.println(jsCode);
		} catch (IOException ex) {
			throw new JspException(ex);
		}
		
		return super.doEndTag();
	}
	
}
