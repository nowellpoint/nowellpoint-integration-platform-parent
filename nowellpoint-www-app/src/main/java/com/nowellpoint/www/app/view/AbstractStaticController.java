package com.nowellpoint.www.app.view;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.strong;
import static spark.Spark.halt;

import java.io.IOException;
import java.io.StringReader;
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
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import j2html.tags.UnescapedText;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class AbstractStaticController {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractStaticController.class.getName());
	
	protected static final ObjectMapper objectMapper = new ObjectMapper();
	protected static final String APPLICATION_CONTEXT = "/app/%s";
	protected static final String CONSOLE = String.format(APPLICATION_CONTEXT, "main.html");
	protected static final String TOKEN = "com.nowellpoint.auth.token";
	protected static final String IDENTITY = "com.nowellpoint.auth.identity";
	protected static final String LOCALE = "com.nowellpoint.default.locale";
	protected static final String TIME_ZONE = "com.nowellpoint.default.timezone";
	
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
	
	protected static String secureTemplate(String templateName) {
		return String.format(APPLICATION_CONTEXT, templateName);
	}
	
	protected static Map<String, Object> getModel() {
		Map<String, Object> model = new HashMap<>();
		return model;
	}
	
	protected static Token getToken(Request request) {
		return request.attribute(TOKEN);
	}
	
	protected static Identity getIdentity(Request request) {
		return request.attribute(IDENTITY);
	}
	
	protected static Locale getLocale(Request request) {
		return request.attribute(LOCALE);
	}
	
	protected static TimeZone getTimeZone(Request request) {		
		return request.attribute(TIME_ZONE);
	}
	
	protected static String getLabel(Class<?> controllerClass, Request request, String key) {
		return ResourceBundle.getBundle(controllerClass.getName(), getLocale(request)).getString(key);
	}
	
	protected static String render(Class<?> controllerClass, Configuration configuration, Request request, Response response, Map<String,Object> model, String templateName) {	
		Identity identity = getIdentity(request);
		Locale locale = getLocale(request);
		TimeZone timeZone = getTimeZone(request);
		model.put("identity", identity);
		try {
			model.put("messages", new ResourceBundleModel(ResourceBundle.getBundle("messages", locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
			model.put("labels", new ResourceBundleModel(ResourceBundle.getBundle(controllerClass.getName(), locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
			model.put("links", new ResourceBundleModel(ResourceBundle.getBundle("links"), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return showError(e.getMessage());
		}
        return buildTemplate(configuration, locale, timeZone, new ModelAndView(model, templateName));
    }
	
	protected static String responseBody(Class<?> controllerClass, Configuration configuration, Request request, Response response, Map<String,Object> model, String html) {
		model.put("labels", new ResourceBundleModel(ResourceBundle.getBundle(controllerClass.getName(), getLocale(request)), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
		try {
			Template template = new Template("templateName", new StringReader(html), configuration);
			Writer out = new StringWriter();
			template.process(model, out);
			return out.toString();
		} catch (IOException | TemplateException e) {
			return showError(e.getMessage());
		}
	}
	
	protected static String responseBody(Result result) {
		if (result.isSuccess()) {
			return "";
		}
		return showError(result.getErrorMessage());
	}
	
	protected static String showErrorMessage(Class<?> controllerClass, Configuration configuration, Request request, Response response, String errorMessage) {
		Map<String, Object> model = getModel();
		model.put("errorMessage", errorMessage);
		model.put("messages", new ResourceBundleModel(ResourceBundle.getBundle("messages", getLocale(request)), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));
		response.status(400);
		return buildTemplate(configuration, getLocale(request), getTimeZone(request), new ModelAndView(model, String.format(APPLICATION_CONTEXT,"error.html")));
	}
	
	protected static String showError(String errorMessage) {
		return div().withId("error").withClass("alert alert-danger")
				.with(a().withClass("close").withData("dismiss", "alert")
						.with(new UnescapedText("&times;")))
				.with(div().with(strong().withText(errorMessage)))
				.render();
	}
}