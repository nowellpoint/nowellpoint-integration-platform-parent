package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.model.UserAddressRequest;
import com.nowellpoint.console.model.UserPreferenceRequest;
import com.nowellpoint.console.model.UserProfile;
import com.nowellpoint.console.model.UserProfileRequest;
import com.nowellpoint.console.service.UserProfileService;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class UserProfileController extends BaseController {
	
	private static final UserProfileService userProfileService = new UserProfileService();
	
	public static void configureRoutes(Configuration configuration) {
		
		get(Path.Route.USER_PROFILE, (request, response) 
				-> viewUserProfile(configuration, request, response));
		
		post(Path.Route.USER_PROFILE, (request, response) 
				-> updateUserProfile(configuration, request, response));
		
		post(Path.Route.USER_PROFILE_ADDRESS, (request, response) 
				-> updateAddress(configuration, request, response));
		
		post(Path.Route.USER_PROFILE_PREFERENCES, (request, response) 
				-> updateUserPreferences(configuration, request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String viewUserProfile(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		UserProfile userProfile = userProfileService.get(id);
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(UserProfileController.class)
				.putModel("userProfile", userProfile)
				.putModel("locales", getAvailableLocales())
				.putModel("timeZones", getAvailableTimeZones())
				.request(request)
				.templateName(Templates.USER_PROFILE)
				.build();
		
		return template.render();
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String updateUserProfile(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		String firstName = request.queryParams("firstName");
		String lastName = request.queryParams("lastName");
		String title = request.queryParams("title");
		String email = request.queryParams("email");
		String phone = request.queryParams("phone");
		
		UserProfileRequest userProfileRequest = UserProfileRequest.builder()
				.firstName(firstName)
				.lastName(lastName)
				.title(title)
				.email(email)
				.phone(phone)
				.build();
		
		UserProfile userProfile = userProfileService.update(id, userProfileRequest);
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(UserProfileController.class)
				.putModel("userProfile", userProfile)
				.request(request)
				.templateName(Templates.USER_PROFILE_INFORMATION)
				.build();
		
		return template.render();
		
//		if (updateResult.isSuccess()) {
			
			
//		} else {
	//		return null; //showErrorMessage(UserProfileController.class, configuration, request, response, updateResult.getErrorMessage());
//		}
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String updateAddress(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		String city = request.queryParams("city");
		String countryCode = request.queryParams("countryCode");
		String postalCode = request.queryParams("postalCode");
		String state = request.queryParams("state");
		String street = request.queryParams("street");
		
		UserAddressRequest addressRequest = UserAddressRequest.builder()
				.city(city)
				.countryCode(countryCode)
				.postalCode(postalCode)
				.state(state)
				.street(street)
				.build();
		
		UserProfile userProfile = userProfileService.update(id, addressRequest);
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(UserProfileController.class)
				.putModel("userProfile", userProfile)
				.request(request)
				.templateName(Templates.USER_PROFILE_ADDRESS)
				.build();
		
		return template.render();
	}
	
	private static String updateUserPreferences(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		String locale = request.queryParams("locale");
		String timeZone = request.queryParams("timeZone");
		
		UserPreferenceRequest userPreferenceRequest = UserPreferenceRequest.builder()
				.locale(locale)
				.timeZone(timeZone)
				.build();
		
		UserProfile userProfile = userProfileService.update(id, userPreferenceRequest);
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(UserProfileController.class)
				.putModel("userProfile", userProfile)
				.request(request)
				.templateName(Templates.USER_PROFILE_PREFERENCES)
				.build();
		
		return template.render();
		
//		if (updateResult.isSuccess()) {
			
			
//		} else {
	//		return null; //showErrorMessage(UserProfileController.class, configuration, request, response, updateResult.getErrorMessage());
//		}
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
}