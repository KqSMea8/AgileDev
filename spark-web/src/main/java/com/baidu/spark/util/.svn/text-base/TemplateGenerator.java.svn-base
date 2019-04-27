package com.baidu.spark.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * 使用Freemarker模板引擎的邮件内容生成器.
 * 
 * @author GuoLin
 */
public class TemplateGenerator {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/** Freemarker的配置. */
	private Configuration freemarkerConfiguration = null;

	/** 邮件生成的编码方式，默认为UTF-8. */
	private String encode = "UTF-8";

	/** 模板文件存放的路径(在classpath下). */
	private String basePath;
	
	/** 全局Model. */
	private Map<String, Object> globalModel;
	
	/**
	 * 构造器.
	 */
	public TemplateGenerator() {
		// 注入默认对象
		globalModel = new HashMap<String, Object>();
		globalModel.put("message", new MessageMethodModel());
		globalModel.put("serverUrlPrefix", SparkConfig.getServerUrlPrefix());
	}

	/**
	 * 获取freemarker的配置.
	 * <p>
	 * 如果需要修改配置文件的获取方式,子类可以重写此方法以.
	 * </p>
	 * @return Freemarker的配置
	 */
	protected Configuration getFreemarkerConfiguration() {
		if (freemarkerConfiguration == null) {
			freemarkerConfiguration = new Configuration();
			freemarkerConfiguration.setDefaultEncoding(encode);
			freemarkerConfiguration.setClassForTemplateLoading(this.getClass(), basePath);
			freemarkerConfiguration.setNumberFormat("#");
		}
		return freemarkerConfiguration;
	}

	/**
	 * 根据模板文件和传入的属性生成邮件内容.
	 * @param model 模板生成时的键值映射
	 * @param templateFile 指定使用的模板
	 * @return 生成的内容
	 */
	public String generate(Map<String, Object> model, String templateFile) {
		// 合并用户参数和全局参数
		Map<String, Object> propModel = new HashMap<String, Object>();
		propModel.putAll(globalModel);
		propModel.putAll(model);
		
		try {
			Template template = getFreemarkerConfiguration().getTemplate(templateFile, MessageHolder.getLocale());
			return FreeMarkerTemplateUtils.processTemplateIntoString(template, propModel);
		} catch (TemplateException ex) {
			logger.error("Error while processing FreeMarker template ", ex);
		} catch (FileNotFoundException ex) {
			logger.error("Error while open template file ", ex);
		} catch (IOException ex) {
			logger.error("Error while generate content ", ex);
		}
		return null;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}
	
	/**
	 * Freemarker国际化方法模型类.
	 * 
	 * @author GuoLin
	 *
	 */
	private static class MessageMethodModel implements TemplateMethodModelEx {

		@Override 
		@SuppressWarnings("rawtypes")
		public Object exec(List arguments) throws TemplateModelException {
			String code = arguments.get(0).toString();
			if (arguments.size() > 1) {
				Object[] args = new Object[arguments.size() - 1];
				for (int i = 1; i < arguments.size(); i++) {
					args[i - 1] = arguments.get(i).toString();
				}
				return MessageHolder.get(code, args);
			} else {
				return MessageHolder.get(code);
			}
		}
		
	}

}
