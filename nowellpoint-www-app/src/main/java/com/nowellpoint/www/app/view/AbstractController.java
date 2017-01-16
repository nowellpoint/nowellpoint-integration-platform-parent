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
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Token;

import freemarker.core.Environment;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

abstract class AbstractController implements Controller {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractController.class.getName());
	protected static final String API_ENDPOINT = System.getenv("NOWELLPOINT_API_ENDPOINT");
	protected static final ObjectMapper objectMapper = new ObjectMapper();
	protected static final String APPLICATION_CONTEXT = "/app/%s";
	private Class<?> controllerClass;
	
	public AbstractController(Class<?> controllerClass) {		
		this.controllerClass = controllerClass;
	}
	
	protected Map<String, Object> getModel() {
		Map<String, Object> model = new HashMap<String, Object>();
		return model;
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
	
	private String buildTemplate(Configuration configuration, Locale locale, TimeZone timeZone, ModelAndView modelAndView) {
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
	
	protected Token getToken(Request request) {
		return request.attribute("com.nowellpoint.auth.token");
	}
	
	protected Identity getIdentity(Request request) {
		return request.attribute("account");
	}
	
	protected Locale getLocale(Request request) {
		return request.attribute("com.nowellpoint.default.locale");
	}
	
	protected TimeZone getTimeZone(Request request) {		
		return request.attribute("com.nowellpoint.default.timezone");
	}
	
	protected String getLabel(Request request, String key) {
		return ResourceBundle.getBundle(controllerClass.getName(), getLocale(request)).getString(key);
	}
	
	protected String render(Configuration configuration, Request request, Response response, Map<String,Object> model, String templateName) {	
		Identity identity = getIdentity(request);
		Locale locale = getLocale(request) != null ? getLocale(request) : configuration.getLocale();
		TimeZone timeZone = getTimeZone(request) != null ? getTimeZone(request) : configuration.getTimeZone();
		model.put("account", identity);
        model.put("messages", new ResourceBundleModel(ResourceBundle.getBundle("messages", locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
        model.put("labels", new ResourceBundleModel(ResourceBundle.getBundle(controllerClass.getName(), locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
        return buildTemplate(configuration, locale, timeZone, new ModelAndView(model, templateName));
    }
}