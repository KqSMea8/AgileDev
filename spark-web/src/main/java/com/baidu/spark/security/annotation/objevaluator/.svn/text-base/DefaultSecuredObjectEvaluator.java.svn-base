package com.baidu.spark.security.annotation.objevaluator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import com.baidu.spark.security.annotation.SecuredMethod;
import com.baidu.spark.security.annotation.SecuredObj;
/**
 * 默认的解析器
 * @author zhangjing_pe
 *
 */
@Service
public class DefaultSecuredObjectEvaluator extends BaseSecuredObjectEvaluator{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected void setSecuredObjectInfo(Method method,Object[] args,SecuredMethod securedMethodAnno){
		Annotation[][] paramsAnnotations = method.getParameterAnnotations();
		//只获取第一个SecuredObj标识的对象
		boolean foundParamAnnotation = false;
		for(int i=0;i<args.length;i++){
			if (foundParamAnnotation) {
				break;
			}
			Object arg = args[i];
			Annotation[] annos = paramsAnnotations[i];
			for(Annotation anno:annos){
				if(anno instanceof SecuredObj){
					SecuredObj securedObjAnno = (SecuredObj)anno;
					String identityProperty = securedObjAnno.idPropertyName();
					if(securedObjAnno.clazz().equals(void.class)){
						if(arg == null){
							//clazz为空且参数值也为空
							securedObjClass = null;
							securedObjId = null;
							foundParamAnnotation = true;
							break;
						}
						//若未显式地定义clazz，表示被注释的对象是资源实体对象，需获取其主键id
						securedObjClass = arg.getClass();
						//若identityProperty声明为空，则默认使用id属性获取
						if(StringUtils.isEmpty(identityProperty)){
							identityProperty = "id";
						}
						securedObjId = getObjectIdentity(arg, identityProperty);
					}else{
						securedObjClass = securedObjAnno.clazz();
						if(StringUtils.isEmpty(identityProperty)){
							if(arg != null){
								securedObjId = arg.toString();
							}else{
								securedObjId = null;
							}
							
						}else{
							securedObjId = getObjectIdentity(arg, identityProperty);	
						}
					}
					foundParamAnnotation = true;
					break;
				}
			}
		}
		if (!foundParamAnnotation){
			throw new AuthorizationServiceException("NO \"SecuredObj\" annotation found!");
		}
	}
}
