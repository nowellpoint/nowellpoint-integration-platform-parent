package com.nowellpoint.www.app.view;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.Address;
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
		public static final String USER_PROFILE_ME = String.format(APPLICATION_CONTEXT, "user-profile-me.html");
		public static final String USER_PROFILE = String.format(APPLICATION_CONTEXT, "user-profile.html");
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
		
		Map<String, Object> model = getModel();
		model.put("userProfile", userProfile);
		model.put("successMessage", request.cookie("update.profile.success"));
		model.put("locales", new TreeMap<String, String>(getLocales(identity.getLocale())));
		model.put("timeZones", getTimeZones());
		
		if (userProfile.getId().equals(identity.getId())) {
			return render(UserProfileController.class, configuration, request, response, model, Template.USER_PROFILE_ME);
		}

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
		
		UserProfileRequest userProfileRequest = new UserProfileRequest()
				.withFirstName(request.queryParams("firstName"))
				.withLastName(request.queryParams("lastName"))
				.withCompany(request.queryParams("company"))
				.withDivision(request.queryParams("division"))
				.withDepartment(request.queryParams("department"))
				.withTitle(request.queryParams("title"))
				.withEmail(request.queryParams("email"))
				.withMobilePhone(request.queryParams("mobilePhone"))
				.withPhone(request.queryParams("phone"))
				.withExtension(request.queryParams("extension"))
				.withLocale(request.queryParams("locale"))
				.withTimeZone(request.queryParams("timeZone"));

		UpdateResult<UserProfile> updateResult = NowellpointClient.defaultClient(token)
				.userProfile()
				.update(request.params(":id"), userProfileRequest);
		
		if (! updateResult.isSuccess()) {
			response.status(400);
		}
		
		return responseBody(updateResult);
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
		
		UpdateResult<Address> updateResult = NowellpointClient.defaultClient(token)
				.userProfile()
				.address()
				.update(request.params(":id"), addressRequest);
		
		if (! updateResult.isSuccess()) {
			response.status(400);
		}
		
		return responseBody(updateResult);
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