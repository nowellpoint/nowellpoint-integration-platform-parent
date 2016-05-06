package com.nowellpoint.www.app.view;

import java.util.HashMap;
import java.util.Map;

import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.util.ResourceBundleUtil;

import freemarker.template.Configuration;
import spark.Request;

abstract class AbstractController {
	
	private Class<?> controllerClass;
	private Configuration configuration;
	
	public AbstractController(Class<?> controllerClass, Configuration configuration) {		
		this.controllerClass = controllerClass;
		this.configuration = configuration;
	}
	
	protected Map<String, Object> getModel() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("labels", ResourceBundleUtil.getResourceBundle(controllerClass.getName(), configuration.getLocale()));
		return model;
	}
	
	protected Token getToken(Request request) {
		Token token = request.attribute("token");
		return token;
	}
	
	protected Account getAccount(Request request) {
		Account account = request.attribute("account");
		return account;
	}
	
	protected String getBodyFromQueryParams(Request request) {
		StringBuilder sb = new StringBuilder();
		request.queryParams().stream().forEach(p-> {
			if (! request.queryParams(p).isEmpty()) {
				sb.append(p);
				sb.append("=");
				sb.append(request.queryParams(p));
				sb.append("&");
			}
		});
		return sb.toString();
	}
}