package com.nowellpoint.listener.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;

public class JsonbUtil {
	
	private static final Jsonb JSONB = JsonbBuilder.create(getJsonbConfig());
	
	private static JsonbConfig getJsonbConfig() {
		return new JsonbConfig()
				.withNullValues(Boolean.TRUE)
				.withPropertyVisibilityStrategy(
						new PropertyVisibilityStrategy() {
							
							@Override
							public boolean isVisible(Field field) {
								return true;
							}
							
							@Override
							public boolean isVisible(Method method) {
								return false;
							}
							
						});
	}

	public static Jsonb getJsonb() {
		return JSONB;
	}
}