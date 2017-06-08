package com.nowellpoint.www.app.util;

import static spark.Spark.halt;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

import com.nowellpoint.client.model.Identity;

import freemarker.core.Environment;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import spark.ModelAndView;

public class TemplateBuilder {
	
	private static final Logger LOGGER = Logger.getLogger(TemplateBuilder.class.getName());
	
	private Identity identity;
	private Locale locale;
	private TimeZone timeZone;
	private Class<?> controllerClass;
	private Configuration configuration;
	private Map<String,Object> model;
	private String templateName;
	
	private TemplateBuilder() {
		model = new HashMap<>();
	}
	
	public static TemplateBuilder template() {
		return new TemplateBuilder();
	}
	
	public TemplateBuilder identity(Identity identity) {
		this.identity = identity;
		return this;
	}

	public TemplateBuilder locale(Locale locale) {
		this.locale = locale;
		return this;
	}
	
	public TemplateBuilder timeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
		return this;
	}
	
	public TemplateBuilder controllerClass(Class<?> controllerClass) {
		this.controllerClass = controllerClass;
		return this;
	}
	
	public TemplateBuilder configuration(Configuration configuration) {
		this.configuration = configuration;
		return this;
	}
	
	public TemplateBuilder model(Map<String,Object> model) {
		this.model = model;
		return this;
	}
	
	public TemplateBuilder addToModel(String key, Object value) {
		model.put(key, value);
		return this;
	}
	
	public TemplateBuilder templateName(String templateName) {
		this.templateName = templateName;
		return this;
	}
	
	public String build() {
		model.put("identity", identity);
		try {
			model.put("messages", new ResourceBundleModel(ResourceBundle.getBundle("messages", locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
			model.put("labels", new ResourceBundleModel(ResourceBundle.getBundle(controllerClass.getName(), locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
			model.put("links", new ResourceBundleModel(ResourceBundle.getBundle("links"), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
		} catch (Exception e) {
			LOGGER.error("TemplateBuilderException", e);
		}
		
		ModelAndView modelAndView = new ModelAndView(model, templateName);
        
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