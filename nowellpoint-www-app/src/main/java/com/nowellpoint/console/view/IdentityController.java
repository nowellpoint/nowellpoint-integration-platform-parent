package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.IdentityRequest;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class IdentityController extends BaseController {
		
	public static void configureRoutes(Configuration configuration) {
		
		get(Path.Route.IDENTITY, (request, response) 
				-> viewIdentity(request, response));
		
		post(Path.Route.IDENTITY, (request, response) 
				-> updateUserInformation(configuration, request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String viewIdentity(Request request, Response response) {
		
		String identityId = getIdentity(request).getId();
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.get(identityId);
		
		Map<String, Object> model = getModel();
		model.put("identity", identity);
		model.put("locales", getAvailableLocales());
		model.put("timeZones", getAvailableTimeZones());
		
		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(IdentityController.class)
				.model(model)
				.templateName(Templates.IDENTITY)
				.build();
		
		return processTemplate(templateProcessRequest);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String updateUserInformation(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		String firstName = request.queryParams("firstName");
		String lastName = request.queryParams("lastName");
		String email = request.queryParams("email");
		String timeZone = request.queryParams("timeZone");
		Locale locale = convertStringToLocale(request.queryParams("locale"));
		
		IdentityRequest identityRequest = IdentityRequest.builder()
				.firstName(firstName)
				.lastName(lastName)
				.timeZone(timeZone)
				.email(email)
				.locale(locale)
				.build();
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.update(id, identityRequest);
		
		Map<String, Object> model = new HashMap<>();
		model.put("userProfile", identity);
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(IdentityController.class)
				.model(model)
				.request(request)
				.templateName(Templates.IDENTITY_INFORMATION)
				.build();
		
		return template.render();
	}
	
	/**
	 * 
	 * @param accountProfile
	 * @return Locale map 
	 */
	
	private static Map<String,String> getAvailableLocales() {		
		Map<String,String> localeMap = Arrays.asList(Locale.getAvailableLocales())
				.stream()
				.collect(Collectors.toMap(l -> l.toString(), l -> l.getDisplayLanguage()
						.concat(! l.getCountry().isEmpty() ? " (".concat(l.getDisplayCountry().concat(")")) : "")));
		
		return localeMap;
	}
	
	/**
	 * 
	 * @return application supported timezones
	 */
	
	private static List<String> getAvailableTimeZones() {
		return Arrays.asList(TimeZone.getAvailableIDs());
	}
	
	/**
	 * 
	 * @param localeString
	 * @return
	 */
	
	private static Locale convertStringToLocale(String localeString) {
		if (localeString == null || localeString.isEmpty()) {
			return Locale.getDefault();
		}
		String[] tokens = localeString.split("_");
		if (tokens.length == 1) {
			return new Locale(tokens[0]);
		} else if (tokens.length == 2) {
			return new Locale(tokens[0], tokens[1]);
		} else if (tokens.length == 3) {
			return new Locale(tokens[0], tokens[1], tokens[2]);
		} else {
			return null;
		}
	}
}