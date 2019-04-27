package com.baidu.spark.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * spark的输出显示
 * 
 * @author zhangjing_pe
 * @author shixiaolei
 */
public class OutTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	private Object value;
	// 将字符串转化为json map中的值的字符串
	private boolean jsonValueStyle;
	// 当value为空时，要使用的默认值
	private String defaultValue;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		try {
			if (jsonValueStyle == true) {
				out.print(com.baidu.spark.util.StringUtils.getEscapedJsonString(value, defaultValue).replace("<script>", "&lt;script&gt;").replace("</script>", "&lt;/script&gt;")); 
			} else {
				if (value == null) {
					if(defaultValue!=null){
						out.print(defaultValue);
					}else{
						out.print(value.toString());	
					}
				}
			}
		} catch (IOException e) {
			throw new JspException(e);
		}
		return super.doEndTag();
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isJsonValueStyle() {
		return jsonValueStyle;
	}

	public void setJsonValueStyle(boolean jsonValueStyle) {
		this.jsonValueStyle = jsonValueStyle;
	}

	public String getDefault() {
		return defaultValue;
	}

	public void setDefault(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
