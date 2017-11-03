package com.nowellpoint.www.app.view;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.Address;
import com.nowellpoint.client.model.AddressRequest;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.UserProfile;
import com.nowellpoint.client.model.UserProfileRequest;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class UserProfileController extends AbstractStaticController {
	
	public static class Template {
		public static final String USER_PROFILE_ME = String.format(APPLICATION_CONTEXT, "user-profile-me.html");
		public static final String USER_PROFILE = String.format(APPLICATION_CONTEXT, "user-profile.html");
		public static final String USER_PROFILE_DEACTIVATE = String.format(APPLICATION_CONTEXT, "user-profile-deactivate.html");
		public static final String USER_PROFILE_PAYMENT_METHOD = String.format(APPLICATION_CONTEXT, "payment-method.html");
		public static final String USER_PROFILE_CURRENT_PLAN = String.format(APPLICATION_CONTEXT, "account-profile-current-plan.html");
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
		
		Address address = new Address();
		address.setCity(identity.getAddress().getCity());
		address.setPostalCode(identity.getAddress().getPostalCode());
		address.setState(identity.getAddress().getState());
		address.setStreet(identity.getAddress().getStreet());
		address.setCountryCode(userProfile.getAddress().getCountryCode());
		
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
	
	public static String downloadInvoice(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String invoiceNumber = request.params(":invoiceNumber");
		
		try {
			byte[] data = NowellpointClient.defaultClient(token)
					.userProfile()
					.downloadInvoice(id, invoiceNumber);
			
			HttpServletResponse httpServletResponse = response.raw();
	        httpServletResponse.setContentType("application/pdf");
	        httpServletResponse.addHeader("Content-Disposition", "inline; filename=mypdf.pdf");
	        httpServletResponse.getOutputStream().write(data);
	        httpServletResponse.getOutputStream().close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
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
	
	public static String confirmDeactivateAccountProfile(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		UserProfile userProfile = NowellpointClient.defaultClient(token)
				.userProfile()
				.get(request.params(":id"));
			
		Map<String, Object> model = getModel();
		model.put("userProfile", userProfile);
			
		return render(UserProfileController.class, configuration, request, response, model, Template.USER_PROFILE_DEACTIVATE);		
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String deactivateAccountProfile(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);

		DeleteResult deleteResult = NowellpointClient.defaultClient(token).userProfile()
				.deactivate(request.params(":id"));

		if (!deleteResult.isSuccess()) {
			throw new BadRequestException(deleteResult.getErrorMessage());
		}

		response.redirect(Path.Route.LOGOUT);

		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String removeProfilePicture(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		UpdateResult<UserProfile> updateResult = NowellpointClient.defaultClient(token)
				.userProfile()
				.removeProfilePicture(request.params(":id"));
		
		if (! updateResult.isSuccess()) {
			throw new BadRequestException(updateResult.getErrorMessage());
		}
		
		Map<String, Object> model = getModel();
		model.put("userProfile", updateResult.getTarget());
		
		return render(UserProfileController.class, configuration, request, response, model, Template.USER_PROFILE);
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
		
		AddressRequest addressRequest = new AddressRequest()
				.withCity(request.queryParams("city"))
				.withCountryCode(request.queryParams("countryCode"))
				.withPostalCode(request.queryParams("postalCode"))
				.withState(request.queryParams("state"))
				.withStreet(request.queryParams("street"));
		
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