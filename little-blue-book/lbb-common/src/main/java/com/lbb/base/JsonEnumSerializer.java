package com.lbb.base;




import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName JsonEnumSerializer
 * @Description JsonEnumSerializer枚举基类
 * @Author wqh
 * @Date 2022/11/21 17:02
 * @Version 1.0
 **/
public class JsonEnumSerializer extends JsonSerializer<BaseEnum> {
	@Override
	public void serialize(BaseEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		try {
			//类的全限定名
			String name = value.getClass().getName();
			//类的code值
			Object code = value.getCode();

			String message = value.getMessage();

			//这里也不用反射了,性能不好,所以直接new一个map再做序列化
			Map<String, Object> jsonMap = new HashMap<>();
			jsonMap.put("code", code);
			jsonMap.put("message", message);

			serializers.defaultSerializeValue(jsonMap, gen);
		} catch (Exception e) {
			System.out.println("JsonEnumSerializer serialize error: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
