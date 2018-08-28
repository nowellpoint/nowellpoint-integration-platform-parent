package com.nowellpoint.console.view;

import static spark.Spark.halt;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.TemplateProcessRequest;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.www.app.util.Path;

import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import spark.ModelAndView;

public class TemplateManager {
    private Configuration configuration;
    private static final String TOKEN = "com.nowellpoint.auth.token";
    private static final String IDENTITY = "com.nowellpoint.auth.identity";
    private static final String LOCALE = "com.nowellpoint.default.locale";
    private static final String TIME_ZONE = "com.nowellpoint.default.timezone";

    public TemplateManager() {
    	configuration = new Configuration(Configuration.VERSION_2_3_28);
    	configuration.setTagSyntax(Configuration.ANGLE_BRACKET_TAG_SYNTAX);
    	configuration.setDefaultEncoding("UTF-8");
    	configuration.setNumberFormat("computer");
    	configuration.setClassForTemplateLoading(this.getClass(), "/views");
    	configuration.setObjectWrapper(new BeansWrapperBuilder(Configuration.VERSION_2_3_28).build());
    }

    public String processTemplate(TemplateProcessRequest request) {
    	Token token = request.getRequest().attribute(TOKEN);
		Identity identity = request.getRequest().attribute(IDENTITY);
		Locale locale = request.getRequest().attribute(LOCALE);
		TimeZone timeZone = request.getRequest().attribute(TIME_ZONE);
		
		Map<String,Object> model = new HashMap<String,Object>();
		model.putAll(request.getModel());
		model.put("LOGOUT_URI", Path.Route.LOGOUT);
		model.put("token", token);
		model.put("identity", identity);
		try {
			model.put("messages", new ResourceBundleModel(ResourceBundle.getBundle("messages", locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
			model.put("labels", new ResourceBundleModel(ResourceBundle.getBundle(request.getControllerClass().getName(), locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
			model.put("links", new ResourceBundleModel(ResourceBundle.getBundle("links"), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		ModelAndView modelAndView = new ModelAndView(model, (request.getTemplateName().endsWith(".html") ? 
				request.getTemplateName() : request.getTemplateName() + ".html"));
    	
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