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

import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.util.RequestAttributes;

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
	
	public abstract String getTemplateName();
	public abstract Class<?> getControllerClass();
	public abstract Configuration getConfiguration();
	public abstract Request getRequest();

	@Value.Default
	public Map<String,Object> getModel() {
		return new HashMap<String,Object>();
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
	
	private Token getToken(Request request) {
		return request.attribute(RequestAttributes.AUTH_TOKEN);
	}
	
	private Identity getIdentity(Request request) {
		return request.attribute(RequestAttributes.IDENTITY);
	}
	
	private Locale getLocale(Request request) {
		return request.attribute(RequestAttributes.LOCALE);
	}
	
	private TimeZone getTimeZone(Request request) {		
		return request.attribute(RequestAttributes.TIME_ZONE);
	}
	
	public String render() {	
		Token token = getToken(getRequest());
		Identity identity = getIdentity(getRequest());
		Locale locale = getLocale(getRequest());
		TimeZone timeZone = getTimeZone(getRequest());
		
		Map<String,Object> model = new HashMap<String,Object>();
		model.putAll(getModel());
		model.put("token", token);
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