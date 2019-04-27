package com.baidu.spark.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * UIC注解.
 * 用于配合Spring IoC的Autowired注解使用，标识为需要注入与UIC系统相关的依赖.
 * 
 * 例如在applicationContext文件中配置：
 * <pre>
 * &lt;bean id="uicRestTemplate" class="com.baidu.ecmp.admin.config.RestTemplateFactory"&gt;
 *   &lt;qualifier type="UIC" /&gt;
 *   &lt;property name="hostname" value="jx-iit-dev00.jx.baidu.com" /&gt;
 *   &lt;property name="port" value="7089" /&gt;
 * &lt;/bean&gt;
 * </pre>
 * 便可以在Autowired时使用：
 * <pre>
 * &#064;Autowired
 * &#064;UIC
 * public void setUicRestTemplate(RestTemplate uicRestTemplate) {
 *   this.uicRestTemplate = uicRestTemplate;
 * }
 * </pre>
 * 
 * @author GuoLin
 *
 */
@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface UIC {

}
