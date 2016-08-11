package com.nowellpoint.www.app.view;

import static spark.Spark.halt;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.AccountProfile;

import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import spark.ModelAndView;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

abstract class AbstractController {
	
	protected static final String API_ENDPOINT = System.getenv("NCS_API_ENDPOINT");
	
	private Class<?> controllerClass;
	private ResourceBundleModel messages;
	private ResourceBundleModel labels;
	private Configuration configuration;
	
	public AbstractController(Class<?> controllerClass, Configuration configuration) {		
		this.controllerClass = controllerClass;
		this.configuration = configuration;
		this.labels = new ResourceBundleModel(ResourceBundle.getBundle(controllerClass.getName(), configuration.getLocale()), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build());
		this.messages = new ResourceBundleModel(ResourceBundle.getBundle("messages", configuration.getLocale()), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build());
		configureRoutes(configuration);
	}
	
	public abstract void configureRoutes(Configuration configuration);
	
	protected Map<String, Object> getModel() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("messages", messages);
		model.put("labels", labels);
		return model;
	}
	
	protected String getValue(String key) {
		if (labels.getBundle().containsKey(key)) {
			return labels.getBundle().getString(key);
		} else {
			return messages.getBundle().getString(key);
		}
	}
	
	protected Token getToken(Request request) {
		Token token = request.attribute("token");
		return token;
	}
	
	protected AccountProfile getAccount(Request request) {
		AccountProfile account = request.attribute("account");
		return account;
	}
	
	protected String buildTemplate(ModelAndView modelAndView) {
		Writer output = new StringWriter();
		try {
			Template template = configuration.getTemplate(modelAndView.getViewName());
			template.process(modelAndView.getModel(), output);
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
			halt();
		}
		return output.toString();
	}
	
	public String render(Request request, Map<String,Object> model, String template) {
		AccountProfile accountProfile = request.attribute("account");
		Locale locale = null;
		if (accountProfile.getLocaleSidKey() != null) {
			locale = new Locale(accountProfile.getLocaleSidKey());
		} else {
			locale = configuration.getLocale();
		}
        model.put("messages", new ResourceBundleModel(ResourceBundle.getBundle("messages", locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
        model.put("labels", new ResourceBundleModel(ResourceBundle.getBundle(controllerClass.getName(), locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
        model.put("accountProfile", accountProfile);
       // model.put("WebPath", Path.Web.class); // Access application URLs from templates
        return new FreeMarkerEngine(configuration).render(new ModelAndView(model, template));
    }
}