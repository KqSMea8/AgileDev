package com.baidu.spark.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 受权限保护的资源对象的标识
 * 使用在由{@link SecuredMethod}标注的方法参数中，用于标识资源对象实体
 * <p></p>
 * @author zhangjing_pe
 *
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecuredObj {
	/**
	 * 指定资源类型，用于进行acl权限判断
	 * <p>若被标注的为对象的id，则使用clazz来指示对应的资源类型</p>
	 * <p>若未标注clazz属性，则说明被标注对象即为资源实体</p>
	 * @return 资源对象class
	 */
	public Class<?> clazz() default void.class;
	/**
	 * 从资源对象获取id的方法。默认为“Id”
	 * <p>支持嵌套属性。如：对卡片属性，可配置“space.id”
	 * <p>若未使用clazz指示对应的资源类型，表示被标注对象即为资源实体，那么资源id的获取会默认使用“id”
	 * @return
	 */
	public String idPropertyName() default "";
	
	//TODO 考虑支持对securedObj对象的计算方法。可支持参数到资源对象的转换。如，prefixCode到space的转换。
	//public Class<?> idEvaluator() default Object.class;
	
//	public int position() default 0;
}
