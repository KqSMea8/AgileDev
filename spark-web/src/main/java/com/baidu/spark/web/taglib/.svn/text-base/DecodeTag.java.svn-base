package com.baidu.spark.web.taglib;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * 用于URL反编码
 * 
 * @author shixiaolei
 * 
 */
public class DecodeTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;
	private String value;
	private String enc;

	@Override
	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		try {
			String decodedValue = URLDecoder.decode(value, enc != null ? enc
					: "UTF-8");
			out.print(decodedValue);
		} catch (IOException e) {
			throw new JspException(e);
		}
		return super.doEndTag();
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setEnc(String enc) {
		this.enc = enc;
	}

}
