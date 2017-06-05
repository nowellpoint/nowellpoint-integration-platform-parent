package com.nowellpoint.www.app.util;

import static spark.Spark.halt;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

import com.nowellpoint.client.model.Identity;

import freemarker.core.Environment;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import spark.ModelAndView;

public class TemplateBuilder {
	
	private Identity identity;
	private Locale locale;
	private TimeZone timeZone;
	private Class<?> controllerClass;
	private Configuration configuration;
	private Map<String,Object> model;
	private String templateName;
	
	private TemplateBuilder() {
		
	}
	
	public static TemplateBuilder template() {
		return new TemplateBuilder();
	}
	
	public TemplateBuilder withIdentity(Identity identity) {
		this.identity = identity;
		return this;
	}

	public TemplateBuilder withLocale(Locale locale) {
		this.locale = locale;
		return this;
	}
	
	public TemplateBuilder withTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
		return this;
	}
	
	public TemplateBuilder withControllerClass(Class<?> controllerClass) {
		this.controllerClass = controllerClass;
		return this;
	}
	
	public TemplateBuilder withConfiguration(Configuration configuration) {
		this.configuration = configuration;
		return this;
	}
	
	public TemplateBuilder withModel(Map<String,Object> model) {
		this.model = model;
		return this;
	}
	
	public TemplateBuilder withTemplateName(String templateName) {
		this.templateName = templateName;
		return this;
	}
	
	public String build() {
		model.put("identity", identity);
        model.put("messages", new ResourceBundleModel(ResourceBundle.getBundle("messages", locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
        model.put("labels", new ResourceBundleModel(ResourceBundle.getBundle(controllerClass.getName(), locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
        model.put("links", new ResourceBundleModel(ResourceBundle.getBundle("links"), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
        return buildTemplate(configuration, locale, timeZone, new ModelAndView(model, templateName));
	}
	
	private static String buildTemplate(Configuration configuration, Locale locale, TimeZone timeZone, ModelAndView modelAndView) {
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
}