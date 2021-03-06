package com.nowellpoint.console.view;

import java.util.HashMap;
import java.util.Map;

import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.util.RequestAttributes;

import spark.Request;

public class BaseController2 {
	
	public Token getToken(Request request) {
		return RequestAttributes.getToken(request);
	}
	
	public Identity getIdentity(Request request) {
		return RequestAttributes.getIdentity(request);
	}
	
	public Map<String, Object> getModel() {
		return new HashMap<String, Object>();
	}
	
	public String processTemplate(ProcessTemplateRequest request) {
		return new TemplateManager().processTemplate(request);
	}
}