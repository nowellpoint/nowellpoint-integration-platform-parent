package com.nowellpoint.console.model;

import java.util.HashMap;
import java.util.Map;

import org.immutables.value.Value;

import spark.Request;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractTemplateProcessRequest {
	public abstract String getTemplateName();
	public abstract Class<?> getControllerClass();
	public abstract Request getRequest();

	@Value.Default
	public Map<String,Object> getModel() {
		return new HashMap<String,Object>();
	}
}