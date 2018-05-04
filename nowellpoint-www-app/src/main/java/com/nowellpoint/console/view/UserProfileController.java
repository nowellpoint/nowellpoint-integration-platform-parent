package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.nowellpoint.client.model.AddressRequest;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.UserProfileRequest;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.model.UserProfile;
import com.nowellpoint.console.service.UserProfileService;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class UserProfileController extends BaseController {
	
	private static final UserProfileService userProfileService = new UserProfileService();
	
	public static void configureRoutes(Configuration configuration) {
		
		get(Path.Route.USER_PROFILE_VIEW,
				(request, response) -> viewUserProfile(configuration, request, response));
		
		post(Path.Route.USER_PROFILE_VIEW,
				(request, response) -> updateUserProfile(configuration, request, response));
		
		post(Path.Route.USER_PROFILE_ADDRESS,
				(request, response) -> updateAddress(configuration, request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String viewUserProfile(Configuration configuration, Request request, Response response) {
		Identity identity = getIdentity(request);
		
		String id = request.params(":id");
		
		UserProfile userProfile = userProfileService.get(id);
		
		Boolean readonly = ! userProfile.getId().equals(identity.getUserId());
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(UserProfileController.class)
				.putModel("userProfile", userProfile)
				.putModel("locales", new TreeMap<String, String>(getLocales(identity.getLocale())))
				.putModel("timeZones", getTimeZones())
				.putModel("timeZones", getTimeZones())
				.putModel("readonly", readonly)
				//.putModel("content", Templates.USER_PROFILE)
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
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		String firstName = request.queryParams("firstName");
		String lastName = request.queryParams("lastName");
		String title = request.queryParams("title");
		String email = request.queryParams("email");
		String phone = request.queryParams("phone");
		String locale = request.queryParams("locale");
		String timeZone = request.queryParams("timeZone");
		
		UserProfileRequest userProfileRequest = UserProfileRequest.builder()
				.firstName(firstName)
				.lastName(lastName)
				.title(title)
				.email(email)
				.phone(phone)
				.locale(locale)
				.timeZone(timeZone)
				//.token(token)
				.build();

		UpdateResult<UserProfile> updateResult = null;
		
		Boolean readonly = ! updateResult.getTarget().getId().equals(identity.getId());
		
		if (updateResult.isSuccess()) {
			Map<String, Object> model = new HashMap<>();
			model.put("userProfile", updateResult.getTarget());
			model.put("locales", new TreeMap<String, String>(getLocales(identity.getLocale())));
			model.put("timeZones", getTimeZones());
			model.put("readonly", readonly);
			
			Template template = Template.builder()
					.configuration(configuration)
					.controllerClass(UserProfileController.class)
					.request(request)
					.templateName(Templates.USER_PROFILE)
					.build();
			
			return template.render();
			
		} else {
			return null; //showErrorMessage(UserProfileController.class, configuration, request, response, updateResult.getErrorMessage());
		}
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String updateAddress(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		String userProfileId = request.params(":id");
		
		String city = request.queryParams("city");
		String countryCode = request.queryParams("countryCode");
		String postalCode = request.queryParams("postalCode");
		String state = request.queryParams("state");
		String street = request.queryParams("street");
		
		AddressRequest addressRequest = AddressRequest.builder()
				.city(city)
				.countryCode(countryCode)
				.userProfileId(userProfileId)
				.postalCode(postalCode)
				.state(state)
				.street(street)
				//.token(token)
				.build();
		
		UpdateResult<UserProfile> updateResult = null;
		
		Boolean readonly = ! updateResult.getTarget().getId().equals(identity.getId());
		
		if (updateResult.isSuccess()) {
			Map<String, Object> model = new HashMap<>();
			model.put("userProfile", updateResult.getTarget());
			model.put("locales", new TreeMap<String, String>(getLocales(identity.getLocale())));
			model.put("timeZones", getTimeZones());
			model.put("readonly", readonly);
			
			Template template = Template.builder()
					.configuration(configuration)
					.controllerClass(UserProfileController.class)
					.request(request)
					.templateName(Templates.USER_PROFILE)
					.build();
			
			return template.render();
			
		} else {
			return null; //showErrorMessage(UserProfileController.class, configuration, request, response, updateResult.getErrorMessage());
		}
	}
	
	/**
	 * 
	 * @param accountProfile
	 * @return Locale map 
	 */
	
	private static Map<String,String> getLocales(Locale locale) {
		Locale.setDefault(locale);
		
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
	
	private static List<String> getTimeZones() {
		return Arrays.asList(TimeZone.getAvailableIDs());
	}
}