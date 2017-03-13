package com.nowellpoint.www.app.view;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.strong;
import static spark.Spark.halt;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Result;
import com.nowellpoint.client.model.Token;

import freemarker.core.Environment;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import j2html.tags.UnescapedText;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class AbstractStaticController {
	
	protected static final ObjectMapper objectMapper = new ObjectMapper();
	protected static final String APPLICATION_CONTEXT = "/app/%s";
	
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
	
	protected static Map<String, Object> getModel() {
		Map<String, Object> model = new HashMap<>();
		return model;
	}
	
	protected static Token getToken(Request request) {
		return request.attribute("com.nowellpoint.auth.token");
	}
	
	protected static Identity getIdentity(Request request) {
		return request.attribute("com.nowellpoint.auth.identity");
	}
	
	protected static Locale getLocale(Request request) {
		return request.attribute("com.nowellpoint.default.locale") != null ? request.attribute("com.nowellpoint.default.locale") : Locale.getDefault();
	}
	
	protected static TimeZone getTimeZone(Request request) {		
		return request.attribute("com.nowellpoint.default.timezone") != null ? request.attribute("com.nowellpoint.default.timezone") : TimeZone.getDefault();
	}
	
	protected static String getLabel(Class<?> controllerClass, Request request, String key) {
		return ResourceBundle.getBundle(controllerClass.getName(), getLocale(request)).getString(key);
	}
	
	protected static String render(Class<?> controllerClass, Configuration configuration, Request request, Response response, Map<String,Object> model, String templateName) {	
		Identity identity = getIdentity(request);
		Locale locale = getLocale(request);
		TimeZone timeZone = getTimeZone(request);
		model.put("identity", identity);
        model.put("messages", new ResourceBundleModel(ResourceBundle.getBundle("messages", locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
        model.put("labels", new ResourceBundleModel(ResourceBundle.getBundle(controllerClass.getName(), locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
        model.put("links", new ResourceBundleModel(ResourceBundle.getBundle("links"), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
        return buildTemplate(configuration, locale, timeZone, new ModelAndView(model, templateName));
    }
	
	protected static String response(Result result) {
		if (result.isSuccess()) {
			return "";
		}
		return div().withId("error").withClass("alert alert-danger")
				.with(a().withClass("close").withData("dismiss", "alert")
						.with(new UnescapedText("&times;")))
				.with(div().withClass("text-center")
						.with(strong().withText(result.getErrorMessage())))
				.render();
	}
}