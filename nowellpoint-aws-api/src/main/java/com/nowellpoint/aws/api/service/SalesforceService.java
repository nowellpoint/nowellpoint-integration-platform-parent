package com.nowellpoint.aws.api.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.api.dto.sforce.OrganizationInfo;
import com.nowellpoint.aws.api.dto.sforce.UserInfo;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.sforce.Identity;
import com.nowellpoint.aws.model.sforce.Organization;
import com.nowellpoint.aws.model.sforce.Token;

public class SalesforceService extends AbstractCacheService {
	
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
	
	public Token getToken(String subject, String authCode) {
		Token token = null;
		
		try {
			HttpResponse httpResponse = RestResource.post(System.getProperty(Properties.SALESFORCE_TOKEN_URI))
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
			
			String userId = token.getId().substring(token.getId().lastIndexOf("/") + 1);
			
			LOGGER.info("Salesforce UserId authenticate: " + userId);
			
			hset( subject, Token.class.getName().concat( userId ), token);
			//expire( subject, 3600 );
			
		} catch (IOException e) {
			LOGGER.error( "getIdentity", e.getCause() );
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
			
		return token;
	}
	
	/**
	 * 
	 * @param subject
	 * @return
	 */
	
	public Token findToken(String subject, String userId) {
		Token token = hget( Token.class, subject, Token.class.getName().concat( userId ) );
		return token;
	}
	
	/**
	 * 
	 * @param authCode
	 * @return userInfo
	 */
	
	public UserInfo getUserInfo(Token token) {
		Identity identity = getIdentity(token.getAccessToken(), token.getId());
		
		Organization organization = getOrganization(token.getAccessToken(), identity.getOrganizationId(), identity.getUrls().getSobjects());
		
		UserInfo userInfo = new UserInfo();
		userInfo.setCity(identity.getAddrCity());
		userInfo.setCountry(identity.getAddrCountry());
		userInfo.setDisplayName(identity.getDisplayName());
		userInfo.setEmail(identity.getEmail());
		userInfo.setFirstName(identity.getFirstName());
		userInfo.setUserId(identity.getUserId());
		userInfo.setLanguage(identity.getLanguage());
		userInfo.setLastName(identity.getLastName());
		userInfo.setLocale(identity.getLocale());
		userInfo.setMobilePhone(identity.getMobilePhone());
		userInfo.setPhotos(identity.getPhotos());
		userInfo.setState(identity.getAddrState());
		userInfo.setStreet(identity.getAddrStreet());
		userInfo.setUrls(identity.getUrls());
		userInfo.setUsername(identity.getUsername());
		userInfo.setUtcOffset(identity.getUtcOffset());
		userInfo.setZipPostalCode(identity.getAddrZip());
		
		OrganizationInfo organizationInfo = new OrganizationInfo();
		organizationInfo.setDefaultLocaleSidKey(organization.getDefaultLocaleSidKey());
		organizationInfo.setDivision(organization.getDivision());
		organizationInfo.setFax(organization.getFax());
		organizationInfo.setFiscalYearStartMonth(organization.getFiscalYearStartMonth());
		organizationInfo.setOrganizationId(organization.getId());
		organizationInfo.setInstanceName(organization.getInstanceName());
		organizationInfo.setInstanceUrl(token.getInstanceUrl());
		organizationInfo.setIsSandbox(organization.getIsSandbox());
		organizationInfo.setLanguageLocaleKey(organization.getLanguageLocaleKey());
		organizationInfo.setName(organization.getName());
		organizationInfo.setOrganizationType(organization.getOrganizationType());
		organizationInfo.setPhone(organization.getPhone());
		organizationInfo.setPrimaryContact(organization.getPrimaryContact());
		organizationInfo.setUsesStartDateAsFiscalYearName(organization.getUsesStartDateAsFiscalYearName());
		
		userInfo.setOrganization(organizationInfo);
		
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
			LOGGER.error( "getOrganization", e );
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
		
		return organization;
	}
	
	public List<String> describe(String subject, String userId) {
		
		Token token = hget( Token.class, subject, Token.class.getName().concat(userId) );
		
		String describeGlobal = token.getInstanceUrl().concat("/services/data/v35.0/sobjects");
		
		try {
			HttpResponse httpResponse = RestResource.get(describeGlobal)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.execute();
			
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " : " + httpResponse.getURL());
			
			if (httpResponse.getStatusCode() >= 400) {
				throw new WebApplicationException(httpResponse.getAsString(), httpResponse.getStatusCode());
			}
			
			System.out.println(httpResponse.getEntity(JsonNode.class));
			
		} catch (IOException e) {
			LOGGER.error( "getSObjects", e );
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
		
		return null;
		
	}
}