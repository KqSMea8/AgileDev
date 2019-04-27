package com.baidu.spark.web.taglib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模板Tag.
 * <p>
 * 仿造Freemarker的macro功能实现
 * </p>
 * 
 * @author GuoLin
 * 
 */
public class TemplateTag extends BodyTagSupport implements DynamicAttributes {

	private static final long serialVersionUID = -3207598501419957819L;
	
	/** Http响应状态正常起始值 */
	private static final int HTTP_RESPONSE_STATUS_NORMALLY_START = 200;
	
	/** Http响应状态正常结束值 */
	private static final int HTTP_RESPONSE_STATUS_NORMALLY_END = 299;
	
	/** 默认编码 */
	private static final String DEFAULT_ENCODING = "UTF-8";
	
	/** 模板注入位置标志 */
	private static final String NESTED_TAG = "<!-- #TEMPLATE-BODY# -->";

	/** 页面URL */
	protected String page;
	
	protected String beforeNested = null;
	
	protected String afterNested = null;
	
	protected Map<String, Object> params = new HashMap<String, Object>();
	
	public void setPage(String page) {
		this.page = page;
	}

	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		if (localName != null) {
			params.put("_" + localName, value);
		}
	}

	public int doStartTag() throws JspException {
		
		if (page == null || page.length() == 0) {
			throw new IllegalArgumentException("page");
		}

		JspWriter out = pageContext.getOut();
		
		try {
			String content = acquireString();

			int loc = content.indexOf(NESTED_TAG);
			if (loc < 0) {
				beforeNested = content;
			} else {
				beforeNested = content.substring(0, loc);
				afterNested = content.substring(loc + NESTED_TAG.length() + 1);
				if (afterNested.indexOf(NESTED_TAG) >= 0) {
					throw new JspTagException("Multiple nested tags \"" + NESTED_TAG + "\" were found.");
				}
			}

			out.println(beforeNested);
			return EVAL_BODY_INCLUDE;
		} catch (IOException ex) {
			throw new JspTagException(ex);
		}
	}

	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		try {
			if (afterNested != null) {
				out.println(afterNested);
			}
			return super.doEndTag();
		} catch (IOException ex) {
			throw new JspTagException(ex);
		}
	}

	/**
	 * 获取外部页面被Servlet容器解析后的结果字符串.
	 * @return 结果字符串
	 * @throws IOException 如果找不到文件
	 * @throws JspException 如果遇到JSP解析错误
	 */
	private String acquireString() throws IOException, JspException {

		// 获取并整理URL
		String targetUrl = targetUrl();

		// 创建一个新的PageContext用于隔离变量影响
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			pageContext.getRequest().setAttribute(entry.getKey(), entry.getValue());
		}

		// 使用自定义包装器进行include操作
		ImportResponseWrapper irw = new ImportResponseWrapper(pageContext);
		try {
			RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher(targetUrl);
			rd.include(pageContext.getRequest(), irw);
		} catch (IOException ex) {
			throw new JspException(ex);
		} catch (RuntimeException ex) {
			throw new JspException(ex);
		} catch (ServletException ex) {
			Throwable rc = ex.getRootCause();
			if (rc == null) {
				throw new JspException(ex);
			} else {
				throw new JspException(rc);
			}
		}

		// 校验响应值
		if (irw.getStatus() < HTTP_RESPONSE_STATUS_NORMALLY_START || 
				irw.getStatus() > HTTP_RESPONSE_STATUS_NORMALLY_END) {
			throw new JspTagException(irw.getStatus() + " " + targetUrl);
		}

		return irw.getString();
	}

	/**
	 * 获取解析后的目标URL.
	 * @return 解析后的目标URL
	 */
	private String targetUrl() {
		// TODO 需要处理URL参数逻辑
		
		String targetUrl = page;
		
		if (!targetUrl.startsWith("/")) {
			String sp = ((HttpServletRequest) pageContext.getRequest()).getServletPath();
			targetUrl = sp.substring(0, sp.lastIndexOf('/')) + '/' + targetUrl;
		}
		return targetUrl;
	}

	/**
	 * 响应包装器.
	 * 
	 * @author GuoLin
	 *
	 */
	private class ImportResponseWrapper extends HttpServletResponseWrapper {

		/** The Writer we convey. */
		private StringWriter sw = new StringWriter();

		/** A buffer, alternatively, to accumulate bytes. */
		private ByteArrayOutputStream bos = new ByteArrayOutputStream();

		/** A ServletOutputStream we convey, tied to this Writer. */
		private ServletOutputStream sos = new ServletOutputStream() {
			public void write(int b) throws IOException {
				bos.write(b);
			}
		};

		/** 'True' if getWriter() was called; false otherwise. */
		private boolean isWriterUsed;

		/** 'True if getOutputStream() was called; false otherwise. */
		private boolean isStreamUsed;

		/** The HTTP status set by the target. */
		private int status = HttpServletResponse.SC_OK;

		private PageContext pageContext;

		/** Constructs a new ImportResponseWrapper. */
		public ImportResponseWrapper(PageContext pageContext) {
			super((HttpServletResponse)pageContext.getResponse());
			this.pageContext = pageContext;
		}

		/** Returns a Writer designed to buffer the output. */
		public PrintWriter getWriter() throws IOException {
			if (isStreamUsed) {
				throw new IllegalStateException("Target servlet called getWriter(), then getOutputStream()");
			}
			isWriterUsed = true;
			return new PrintWriterWrapper(sw, pageContext.getOut());
		}

		/** Returns a ServletOutputStream designed to buffer the output. */
		public ServletOutputStream getOutputStream() {
			if (isWriterUsed) {
				throw new IllegalStateException("Target servlet called getOutputStream(), then getWriter()");
			}
			isStreamUsed = true;
			return sos;
		}

		/** Has no effect. */
		public void setContentType(String x) {
			// ignore
		}

		/** Has no effect. */
		public void setLocale(Locale x) {
			// ignore
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public int getStatus() {
			return status;
		}

		/**
		 * Retrieves the buffered output, using the containing tag's
		 * 'charEncoding' attribute, or the tag's default encoding, <b>if
		 * necessary</b>.
		 */
		public String getString() throws UnsupportedEncodingException {
			if (isWriterUsed) {
				return sw.toString();
			} else if (isStreamUsed) {
				return bos.toString(DEFAULT_ENCODING);
			} else {
				return "";
			}
		}
	}

	/**
	 * PrintWriter包装器
	 * 
	 * @author GuoLin
	 *
	 */
	private static class PrintWriterWrapper extends PrintWriter {

		private final Logger logger = LoggerFactory.getLogger(getClass());

		private StringWriter out;
		private Writer parentWriter;

		public PrintWriterWrapper(StringWriter out, Writer parentWriter) {
			super(out);
			this.out = out;
			this.parentWriter = parentWriter;
		}

		public void flush() {
			try {
				parentWriter.write(out.toString());
				StringBuffer sb = out.getBuffer();
				sb.delete(0, sb.length());
			} catch (IOException ex) {
				logger.error("", ex);
			}
		}
	}

}
