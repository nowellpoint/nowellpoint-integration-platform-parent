package com.nowellpoint.console.view;

import java.util.HashMap;
import java.util.Map;

import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.util.RequestAttributes;

import spark.Request;

public class BaseController {
	
	public static Token getToken(Request request) {
		return RequestAttributes.getToken(request);
	}
	
	public static Identity getIdentity(Request request) {
		return RequestAttributes.getIdentity(request);
	}
	
	public static Map<String, Object> getModel() {
		return new HashMap<String, Object>();
	}
}