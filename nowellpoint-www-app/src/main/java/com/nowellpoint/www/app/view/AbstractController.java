package com.nowellpoint.www.app.view;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;

import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import spark.Request;

abstract class AbstractController {
	
	protected static final String API_ENDPOINT = System.getenv("NCS_API_ENDPOINT");
	protected static final String API_KEY = System.getenv("NCS_API_KEY");
	
	private ResourceBundleModel lables;
	
	public AbstractController(Class<?> controllerClass, Configuration configuration) {		
		this.lables = new ResourceBundleModel(ResourceBundle.getBundle(controllerClass.getName(), configuration.getLocale()), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build());
		setupRoutes(configuration);
	}
	
	public abstract void setupRoutes(Configuration configuration);
	
	protected Map<String, Object> getModel() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("labels", lables);
		return model;
	}
	
	protected String getValue(String key) {
		return lables.getBundle().getString(key);
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