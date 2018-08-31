package com.nowellpoint.console.model;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractProcessTemplateRequest {
	public abstract String getTemplateName();
	public abstract Class<?> getControllerClass();
	public abstract @Nullable Identity getIdentity();

	@Value.Default
	public Map<String,Object> getModel() {
		return new HashMap<String,Object>();
	}
}