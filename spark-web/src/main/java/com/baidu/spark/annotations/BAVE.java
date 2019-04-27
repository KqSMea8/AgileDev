package com.baidu.spark.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * BAVE注解. 用于配合Spring IoC的Autowired注解使用，标识为需要注入与BAVE系统相关的依赖.
 * 
 * @author shixiaolei
 * 
 */
@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface BAVE {

}
