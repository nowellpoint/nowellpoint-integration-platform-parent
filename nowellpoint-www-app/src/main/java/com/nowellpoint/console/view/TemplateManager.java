package com.nowellpoint.console.view;

import static spark.Spark.halt;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.util.Path;
import com.nowellpoint.console.util.Templates;

import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import spark.ModelAndView;

public class TemplateManager {
    private Configuration configuration;

    public TemplateManager() {
    	configuration = new Configuration(Configuration.VERSION_2_3_28);
    	configuration.setTagSyntax(Configuration.ANGLE_BRACKET_TAG_SYNTAX);
    	configuration.setDefaultEncoding("UTF-8");
    	configuration.setNumberFormat("computer");
    	configuration.setClassForTemplateLoading(this.getClass(), "/views");
    	configuration.setObjectWrapper(new BeansWrapperBuilder(Configuration.VERSION_2_3_28).build());
    }

    public String processTemplate(ProcessTemplateRequest request) {
    	
		Identity identity = request.getIdentity();
		
		Locale locale = Optional.ofNullable(identity)
				.map(i -> i.getLocale())
				.orElse(Locale.getDefault());
		
		TimeZone timeZone = Optional.ofNullable(identity)
				.map(i -> TimeZone.getTimeZone(i.getTimeZone()))
				.orElse(TimeZone.getDefault());
		
		Map<String,Object> model = new HashMap<String,Object>();
		model.putAll(request.getModel());
		
		/**
		 * Identity check - if authenticated then add all protected URIs else add public URIs
		 */

		if (identity != null) {
			model.put("identity", identity);
			model.put("LOGOUT_URI", Path.Route.LOGOUT);
			model.put("START_URI", Path.Route.START);
			model.put("USER_PROFILE_URI", Path.Route.USER_PROFILE);
			model.put("NOTIFICATIONS_URI", Path.Route.NOTIFICATIONS);
			model.put("ORGANIZATION_URI", Path.Route.ORGANIZATION);
			model.put("EVENT_STREAMS_URI", Path.Route.EVENT_STREAMS);
			model.put("EVENT_STREAMS_SETUP_URI", Path.Route.EVENT_STREAMS_SETUP);
			
			model.put("START", Boolean.FALSE);
			model.put("ORGANIZATION", Boolean.FALSE);
			model.put("EVENT_STREAMS", Boolean.FALSE);
			model.put("NOTIFICATIONS", Boolean.FALSE);
			
			if (Templates.START.equals(request.getTemplateName())) {
				model.put("START", Boolean.TRUE);
			} else if (Templates.ORGANIZATION.equals(request.getTemplateName())) {
				model.put("ORGANIZATION", Boolean.TRUE);
			} else if (Templates.EVENT_STREAMS.equals(request.getTemplateName())) {
				model.put("EVENT_STREAMS", Boolean.TRUE);
			} else if (Templates.NOTIFICATIONS.equals(request.getTemplateName())) {
				model.put("NOTIFICATIONS", Boolean.TRUE);
			}
			
		} else {
			model.put("LOGIN_URI", Path.Route.LOGIN);
		}

		try {
			model.put("messages", new ResourceBundleModel(ResourceBundle.getBundle("messages", locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
			model.put("labels", new ResourceBundleModel(ResourceBundle.getBundle(request.getControllerClass().getName(), locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
			model.put("links", new ResourceBundleModel(ResourceBundle.getBundle("links"), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ModelAndView modelAndView = new ModelAndView(model, request.getTemplateName());
    	
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