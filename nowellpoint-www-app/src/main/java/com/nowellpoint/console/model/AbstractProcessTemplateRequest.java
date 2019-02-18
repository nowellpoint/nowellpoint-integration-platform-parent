package com.nowellpoint.console.model;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.nowellpoint.console.util.UserContext;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractProcessTemplateRequest {
	public abstract String getTemplateName();
	public abstract Class<?> getControllerClass();
	
	@Nullable
	@Value.Default
	public Identity getIdentity() {
		return UserContext.get();
	};

	@Value.Default
	public Map<String,Object> getModel() {
		return new HashMap<String,Object>();
	}
}