package com.nowellpoint.aws.api.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.api.dto.sforce.UserInfo;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.sforce.Identity;
import com.nowellpoint.aws.model.sforce.Organization;
import com.nowellpoint.aws.model.sforce.Token;

public class SalesforceService {
	
	private static final Logger LOGGER = Logger.getLogger(SalesforceService.class);
	
	private static final String ORGANIZATION_FIELDS = "Id,Division,Fax,DefaultLocaleSidKey,FiscalYearStartMonth,"
 			+ "InstanceName,IsSandbox,LanguageLocaleKey,Name,OrganizationType,Phone,PrimaryContact,"
 			+ "UsesStartDateAsFiscalYearName";
	
	public SalesforceService() {

	}
	
	/**
	 * 
	 * @param authCode
	 * @return
	 */
	
	public Token getToken(String authCode) {
		
		System.out.println(authCode);
		Token token = null;
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(System.getProperty(Properties.SALESFORCE_TOKEN_URI))
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.contentType("application/x-www-form-urlencoded")
					.parameter("grant_type", "authorization_code")
					.parameter("code", authCode)
					.parameter("client_id", System.getProperty(Properties.SALESFORCE_CLIENT_ID))
					.parameter("client_secret", System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
					.parameter("redirect_uri", System.getProperty(Properties.SALESFORCE_REDIRECT_URI))
					.execute();
			
			LOGGER.info("Token response status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			if (httpResponse.getStatusCode() >= 400) {
				throw new WebApplicationException(httpResponse.getAsString(), httpResponse.getStatusCode());
			}
			
			token = httpResponse.getEntity(Token.class);
			
		} catch (IOException e) {
			LOGGER.error( "getIdentity", e.getCause() );
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
			
		return token;
	}
	
	/**
	 * 
	 * @param authCode
	 * @return userInfo
	 */
	
	public UserInfo getUserInfo(String authCode) {
		
		Token token = getToken(authCode);
		
		Identity identity = getIdentity(token.getAccessToken(), token.getId());
		
		Organization organization = getOrganization(token.getAccessToken(), identity.getOrganizationId(), identity.getUrls().getSobjects());
		
		UserInfo userInfo = new UserInfo();
		userInfo.setCity(identity.getAddrCity());
		userInfo.setCountry(identity.getAddrCountry());
		userInfo.setDisplayName(identity.getDisplayName());
		userInfo.setEmail(identity.getEmail());
		userInfo.setFirstName(identity.getFirstName());
		userInfo.setId(identity.getUserId());
		userInfo.setLanguage(identity.getLanguage());
		userInfo.setLastName(identity.getLastName());
		userInfo.setLocale(identity.getLocale());
		userInfo.setMobilePhone(identity.getMobilePhone());
		userInfo.setPhotos(identity.getPhotos());
		userInfo.setState(identity.getAddrState());
		userInfo.setStreet(identity.getAddrState());
		userInfo.setUrls(identity.getUrls());
		userInfo.setUsername(identity.getUsername());
		userInfo.setUtcOffset(identity.getUtcOffset());
		userInfo.setZipPostalCode(identity.getAddrZip());
		userInfo.setOrganization(organization);
		
		return userInfo;
		
	}
	
	/**
	 * 
	 * @param bearerToken
	 * @param tokenId
	 * @return identity
	 */
	
	public Identity getIdentity(String bearerToken, String tokenId) {
		Identity identity = null;
		
		try {
			
			HttpResponse httpResponse = RestResource.get(tokenId)
					.acceptCharset(StandardCharsets.UTF_8)
					.bearerAuthorization(bearerToken)
					.accept(MediaType.APPLICATION_JSON)
					.queryParameter("version", "latest")
					.execute();
			
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " : " + httpResponse.getURL());
	    	
	    	if (httpResponse.getStatusCode() >= 400) {
				throw new WebApplicationException(httpResponse.getAsString(), httpResponse.getStatusCode());
			}
	    	
	    	identity = httpResponse.getEntity(Identity.class);
	    	
		} catch (IOException e) {
			LOGGER.error( "getIdentity", e.getCause() );
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
		
		return identity;
	}
	
	/**
	 * 
	 * @param bearerToken
	 * @param organizationId
	 * @param sobjectUrl
	 * @return Organization
	 */
	
	public Organization getOrganization(String bearerToken, String organizationId, String sobjectUrl) {
		Organization organization = null;
		
		try {
	     	
			HttpResponse httpResponse = RestResource.get(sobjectUrl)
	     			.bearerAuthorization(bearerToken)
	     			.path("Organization")
	     			.path(organizationId)
	     			.queryParameter("fields", ORGANIZATION_FIELDS)
	     			.queryParameter("version", "latest")
	     			.execute();
	     	
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " : " + httpResponse.getURL());
			
			if (httpResponse.getStatusCode() >= 400) {
				throw new WebApplicationException(httpResponse.getAsString(), httpResponse.getStatusCode());
			}
	     	
	     	organization = httpResponse.getEntity(Organization.class);
	     	
		} catch (IOException e) {
			LOGGER.error( "getOrganizationByTokenId", e.getCause() );
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
		
		return organization;
	}
}