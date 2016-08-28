package com.nowellpoint.www.app.view;

import static spark.Spark.halt;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.AccountProfile;

import freemarker.core.Environment;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import spark.ModelAndView;
import spark.Request;

abstract class AbstractController {
	
	protected static final String API_ENDPOINT = System.getenv("NCS_API_ENDPOINT");
	protected static final ObjectMapper objectMapper = new ObjectMapper();
	private Class<?> controllerClass;
	private Configuration configuration;
	
	public AbstractController(Class<?> controllerClass, Configuration configuration) {		
		this.controllerClass = controllerClass;
		this.configuration = configuration;
	}
	
	protected Map<String, Object> getModel() {
		Map<String, Object> model = new HashMap<String, Object>();
		return model;
	}
	
	protected Token getToken(Request request) {
		Token token = request.attribute("com.nowellpoint.auth.token");
		return token;
	}
	
	private String buildTemplate(Locale locale, TimeZone timeZone, ModelAndView modelAndView) {
		Writer output = new StringWriter();
		try {
			Template template = configuration.getTemplate(modelAndView.getViewName());
			Environment environment = template.createProcessingEnvironment(modelAndView.getModel(), output);
			environment.setLocale(locale);
			environment.setTimeZone(timeZone);
			environment.process();
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
			halt();
		}
		return output.toString();
	}
	
	protected AccountProfile getAccount(Request request) {
		return request.attribute("account");
	}
	
	protected Locale getDefaultLocale(Request request) {
		AccountProfile accountProfile = getAccount(request);
		
		Locale locale = null;
		if (accountProfile != null && accountProfile.getLocaleSidKey() != null) {
			locale = new Locale(accountProfile.getLocaleSidKey());
		} else {
			locale = configuration.getLocale();
		}
		return locale;
	}
	
	protected TimeZone getDefaultTimeZone(Request request) {
		AccountProfile accountProfile = getAccount(request);
		
		TimeZone timeZone = null;
		if (accountProfile != null && accountProfile.getTimeZoneSidKey() != null) {
			timeZone = TimeZone.getTimeZone(accountProfile.getTimeZoneSidKey());
		} else {
			timeZone = TimeZone.getTimeZone(configuration.getTimeZone().getID());
		}
		
		return timeZone;
	}
	
	protected String render(Request request, Map<String,Object> model, String templateName) {		
		Locale locale = getDefaultLocale(request);
		TimeZone timeZone = getDefaultTimeZone(request);
        model.put("messages", new ResourceBundleModel(ResourceBundle.getBundle("messages", locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
        model.put("labels", new ResourceBundleModel(ResourceBundle.getBundle(controllerClass.getName(), locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
        model.put("account", getAccount(request));
        return buildTemplate(locale, timeZone, new ModelAndView(model, templateName));
    }
}