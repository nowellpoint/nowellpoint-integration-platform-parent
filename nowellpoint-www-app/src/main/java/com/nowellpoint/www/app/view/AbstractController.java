package com.nowellpoint.www.app.view;

import static spark.Spark.halt;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.idp.Token;

import freemarker.core.Environment;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import spark.ModelAndView;
import spark.Request;

abstract class AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractController.class.getName());
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
	
	protected String getValue(Token token, String key) {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.accept(MediaType.TEXT_PLAIN)
				.bearerAuthorization(token.getAccessToken())
				.path("cache")
    			.path(key)
    			.execute();
		
		String value = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			value = httpResponse.getAsString();
		}
		
		return value;
	}
	
	protected void putValue(Token token, String key, String value) {
		HttpResponse httpResponse = RestResource.put(API_ENDPOINT)
				.contentType(MediaType.TEXT_PLAIN)
				.bearerAuthorization(token.getAccessToken())
				.path("cache")
    			.path(key)
    			.body(value)
    			.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			LOGGER.error("Cache exception: " + httpResponse.getAsString());
		}
	}
	
	protected void removeValue(Token token, String key) {
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.contentType(MediaType.TEXT_PLAIN)
				.bearerAuthorization(token.getAccessToken())
				.path("cache")
    			.path(key)
    			.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			LOGGER.error("Cache exception: " + httpResponse.getAsString());
		}
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
	
	protected Locale getDefaultLocale(AccountProfile accountProfile) {
		Locale locale = null;
		if (accountProfile != null && accountProfile.getLocaleSidKey() != null) {
			String[] attrs = accountProfile.getLocaleSidKey().split("_");
			if (attrs.length == 1) {
				locale = new Locale(attrs[0]);
			} else if (attrs.length == 2) {
				locale = new Locale(attrs[0], attrs[1]);
			} else if (attrs.length == 3) {
				locale = new Locale(attrs[0], attrs[1], attrs[3]);
			}
		} else {
			locale = configuration.getLocale();
		}
		return locale;
	}
	
	protected TimeZone getDefaultTimeZone(AccountProfile accountProfile) {		
		TimeZone timeZone = null;
		if (accountProfile != null && accountProfile.getTimeZoneSidKey() != null) {
			timeZone = TimeZone.getTimeZone(accountProfile.getTimeZoneSidKey());
		} else {
			timeZone = TimeZone.getTimeZone(configuration.getTimeZone().getID());
		}
		
		return timeZone;
	}
	
	protected String getLabel(AccountProfile accountProfile, String key) {
		return ResourceBundle.getBundle(controllerClass.getName(), getDefaultLocale(accountProfile)).getString(key);
	}
	
	protected String render(Request request, Map<String,Object> model, String templateName) {	
		AccountProfile accountProfile = getAccount(request);
		Locale locale = getDefaultLocale(accountProfile);
		TimeZone timeZone = getDefaultTimeZone(accountProfile);
		model.put("account", accountProfile);
        model.put("messages", new ResourceBundleModel(ResourceBundle.getBundle("messages", locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
        model.put("labels", new ResourceBundleModel(ResourceBundle.getBundle(controllerClass.getName(), locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
        return buildTemplate(locale, timeZone, new ModelAndView(model, templateName));
    }
}