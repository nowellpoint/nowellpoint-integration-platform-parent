package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.sforce.SalesforceInstance;
import com.nowellpoint.www.app.model.sforce.Urls;
import com.nowellpoint.www.app.model.sforce.Identity;
import com.nowellpoint.www.app.model.sforce.Organization;
import com.nowellpoint.www.app.model.sforce.Photos;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class SalesforceController {
	
	private static final Logger LOGGER = Logger.getLogger(SalesforceController.class.getName());
	
	public SalesforceController(Configuration cfg) {
		
		get("/app/salesforce/oauth", (request, response) -> oauth(request, response));
        
        get("/app/salesforce/callback", (request, response) -> callback(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/salesforce/instance", (request, response) -> getSalesforceInstance(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/salesforce/instance", (request, response) -> saveSalesforceInstance(request, response));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static String oauth(Request request, Response response) {
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
    			.header("x-api-key", System.getenv("NCS_API_KEY"))
    			.path("salesforce")
    			.path("oauth")
    			.queryParameter("state", request.queryParams("id"))
    			.execute();
    	
    	LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL() + " : " + httpResponse.getHeaders().get("Location"));
		
		response.redirect(httpResponse.getHeaders().get("Location").get(0));		
		
		return "";
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static ModelAndView callback(Request request, Response response) {
    	
    	Optional<String> code = Optional.ofNullable(request.queryParams("code"));
    	
    	if (! code.isPresent()) {
    		throw new BadRequestException("missing OAuth code from Salesforce");
    	}
    	
		return new ModelAndView(new HashMap<String, Object>(), "secure/salesforce-callback.html");	
    }
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getSalesforceInstance(Request request, Response response) {
		
		Token token = request.attribute("token");
    	
    	HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
    			.path("salesforce")
    			.path("instance")
    			.queryParameter("code", request.queryParams("code"))
    			.execute();
    	
    	LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
    	
    	SalesforceInstance salesforceInstance = null;
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		salesforceInstance = httpResponse.getEntity(SalesforceInstance.class);	
    	} else {
    		throw new BadRequestException(httpResponse.getAsString());
    	}	
    	
    	Account account = request.attribute("account");
    	
    	Map<String, Object> model = new HashMap<String, Object>();
    	model.put("account", account);
    	model.put("salesforceInstance", salesforceInstance);	
    	
    	return new ModelAndView(model, "secure/salesforce-authenticate.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String saveSalesforceInstance(Request request, Response response) {
		
		Photos photos = new Photos();
		photos.setPicture(request.queryParams("identity.photos.picture"));
		photos.setThumbnail(request.queryParams("identity.photos.thumbnail"));
		
		Urls urls = new Urls();
		//urls.
		
		Identity identity = new Identity();
		identity.setId(request.queryParams("identity.id"));
		identity.setActive(Boolean.valueOf(request.queryParams("identity.active")));
		identity.setAddrCity(request.queryParams("identity.addrCity"));
		identity.setAddrCountry(request.queryParams("identity.addrCountry"));
		identity.setAddrState(request.queryParams("identity.addrState"));
		identity.setAddrStreet(request.queryParams("identity.addrStreet"));
		identity.setAddrZip(request.queryParams("identity.addrZip"));
		identity.setDisplayName(request.queryParams("identity.displayName"));
		identity.setEmail(request.queryParams("identity.email"));
		identity.setFirstName(request.queryParams("identity.firstName"));
		identity.setLanguage(request.queryParams("identity.language"));
		identity.setLastName(request.queryParams("identity.lastName"));
		identity.setLocale(new Locale(request.queryParams("identity.locale")));
		identity.setMobilePhone(request.queryParams("identity.mobilePhone"));
		identity.setNickName(request.queryParams("identity.nickName"));
		identity.setOrganizationId(request.queryParams("identity.organizationId"));
		identity.setUserId(request.queryParams("identity.userId"));
		identity.setUsername(request.queryParams("identity.username"));
		identity.setUserType(request.queryParams("identity.userType"));
		identity.setUtcOffset(request.queryParams("identity.utcOffset"));
		identity.setPhotos(photos);
		identity.setUrls(urls);
		
		Organization organization = new Organization();
		organization.setDefaultLocaleSidKey(request.queryParams("organization.defaultLocaleSidKey"));
		organization.setDivision(request.queryParams("organization.division"));
		organization.setFiscalYearStartMonth(request.queryParams("organization.fiscalYearStartMonth"));
		organization.setId(request.queryParams("organization.id"));
		organization.setInstanceName(request.queryParams("organization.instanceName"));
		organization.setLanguageLocaleKey(request.queryParams("organization.languageLocaleKey"));
		organization.setName(request.queryParams("organization.name"));
		organization.setOrganizationType(request.queryParams("organization.organizationType"));
		organization.setPhone(request.queryParams("organization.phone"));
		organization.setPrimaryContact(request.queryParams("organization.primaryContact"));
		organization.setUsesStartDateAsFiscalYearName(Boolean.valueOf(request.queryParams("identity.usesStartDateAsFiscalYearName")));
		

		SalesforceInstance salesforceInstance = new SalesforceInstance();
		salesforceInstance.setIdentity(identity);
		salesforceInstance.setOrganization(organization);
		
		try {
			System.out.println(new ObjectMapper().writeValueAsString(salesforceInstance));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.redirect("/app/account-profile");

		return "";
	}
}