package com.nowellpoint.www.app.view;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.AddressRequest;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.UserProfile;
import com.nowellpoint.client.model.UserProfileRequest;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class UserProfileController extends AbstractStaticController {
	
	public static class Template {
		public static final String USER_PROFILE = String.format(APPLICATION_CONTEXT, "user-profile.html");
		public static final String USER_PROFILE_CONTENT = String.format(APPLICATION_CONTEXT, "user-profile-content.html");
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String viewUserProfile(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		String id = request.params(":id");
		
		UserProfile userProfile = NowellpointClient.defaultClient(token)
				.userProfile()
				.get(id);
		
		Boolean readonly = ! userProfile.getId().equals(identity.getId());
		
		Map<String, Object> model = getModel();
		model.put("userProfile", userProfile);
		model.put("locales", new TreeMap<String, String>(getLocales(identity.getLocale())));
		model.put("timeZones", getTimeZones());
		model.put("readonly", readonly);

		return render(UserProfileController.class, configuration, request, response, model, Template.USER_PROFILE);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String updateUserProfile(Configuration configuration, Request request, Response response) {
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
				.token(token)
				.build();

		UpdateResult<UserProfile> updateResult = NowellpointClient.defaultClient(token)
				.userProfile()
				.update(request.params(":id"), userProfileRequest);
		
		Boolean readonly = ! updateResult.getTarget().getId().equals(identity.getId());
		
		if (updateResult.isSuccess()) {
			Map<String, Object> model = getModel();
			model.put("userProfile", updateResult.getTarget());
			model.put("locales", new TreeMap<String, String>(getLocales(identity.getLocale())));
			model.put("timeZones", getTimeZones());
			model.put("readonly", readonly);
			return render(UserProfileController.class, configuration, request, response, model, Template.USER_PROFILE_CONTENT);
		} else {
			return showErrorMessage(UserProfileController.class, configuration, request, response, updateResult.getErrorMessage());
		}
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String updateAddress(Configuration configuration, Request request, Response response) {
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
				.token(token)
				.build();
		
		UpdateResult<UserProfile> updateResult = NowellpointClient.defaultClient(token)
				.userProfile()
				.address()
				.update(request.params(":id"), addressRequest);
		
		Boolean readonly = ! updateResult.getTarget().getId().equals(identity.getId());
		
		if (updateResult.isSuccess()) {
			Map<String, Object> model = getModel();
			model.put("userProfile", updateResult.getTarget());
			model.put("locales", new TreeMap<String, String>(getLocales(identity.getLocale())));
			model.put("timeZones", getTimeZones());
			model.put("readonly", readonly);
			return render(UserProfileController.class, configuration, request, response, model, Template.USER_PROFILE_CONTENT);
		} else {
			return showErrorMessage(UserProfileController.class, configuration, request, response, updateResult.getErrorMessage());
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