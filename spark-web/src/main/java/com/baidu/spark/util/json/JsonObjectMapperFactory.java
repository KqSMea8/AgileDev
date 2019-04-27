package com.baidu.spark.util.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * JSON序列化工厂类.
 * 
 * @author GuoLin
 *
 */
public class JsonObjectMapperFactory implements FactoryBean<ObjectMapper> {

	@Override
	public ObjectMapper getObject() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		
		// 创建Serializer
		SerializationConfig serializationConfig = mapper.getSerializationConfig();
		serializationConfig.set(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		serializationConfig.set(SerializationConfig.Feature.AUTO_DETECT_FIELDS, false);
		serializationConfig.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		
		// 创建SerializerFactory并注册Serializer
		CustomSerializerFactory serializerFactory = new CustomSerializerFactory();
		
		// 设定ObjectMapper
		mapper.setSerializerFactory(serializerFactory);
		return mapper;
	}

	@Override
	public Class<ObjectMapper> getObjectType() {
		return ObjectMapper.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}