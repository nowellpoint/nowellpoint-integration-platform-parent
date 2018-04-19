package com.nowellpoint.console.model;

import static spark.Spark.halt;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

import org.immutables.value.Value;

import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Token;

import freemarker.core.Environment;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import spark.ModelAndView;
import spark.Request;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractTemplate {
	
	protected static final String TOKEN = "com.nowellpoint.auth.token";
	protected static final String IDENTITY = "com.nowellpoint.auth.identity";
	protected static final String LOCALE = "com.nowellpoint.default.locale";
	protected static final String TIME_ZONE = "com.nowellpoint.default.timezone";
	
	public abstract String getTemplateName();
	public abstract Class<?> getControllerClass();
	public abstract Configuration getConfiguration();
	public abstract Request getRequest();
	public abstract Map<String,Object> getModel();
	
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
		return request.attribute(TOKEN);
	}
	
	protected Identity getIdentity(Request request) {
		return request.attribute(IDENTITY);
	}
	
	protected Locale getLocale(Request request) {
		return request.attribute(LOCALE);
	}
	
	protected TimeZone getTimeZone(Request request) {		
		return request.attribute(TIME_ZONE);
	}
	
	protected String getLabel(Class<?> controllerClass, Request request, String key) {
		return ResourceBundle.getBundle(controllerClass.getName(), getLocale(request)).getString(key);
	}
	
	public String render() {	
		Identity identity = getIdentity(getRequest());
		Locale locale = getLocale(getRequest());
		TimeZone timeZone = getTimeZone(getRequest());
		Map<String,Object> model = new HashMap<String,Object>();
		model.putAll(getModel());
		model.put("identity", identity);
		try {
			model.put("messages", new ResourceBundleModel(ResourceBundle.getBundle("messages", locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
			model.put("labels", new ResourceBundleModel(ResourceBundle.getBundle(getControllerClass().getName(), locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
			model.put("links", new ResourceBundleModel(ResourceBundle.getBundle("links"), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
		} catch (Exception e) {
			e.printStackTrace();
		}
        return buildTemplate(getConfiguration(), locale, timeZone, new ModelAndView(model, getTemplateName()));
    }
}