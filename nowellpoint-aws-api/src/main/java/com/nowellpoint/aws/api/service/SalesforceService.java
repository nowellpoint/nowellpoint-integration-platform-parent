package com.nowellpoint.aws.api.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.api.dto.sforce.DescribeSObjectsResult;
import com.nowellpoint.aws.api.dto.sforce.ServiceProviderInfo;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.sforce.Identity;
import com.nowellpoint.aws.model.sforce.Organization;
import com.nowellpoint.aws.model.sforce.Token;
import com.nowellpoint.aws.model.sforce.User;

public class SalesforceService extends AbstractCacheService {
	
	private static final Logger LOGGER = Logger.getLogger(SalesforceService.class);
	
	private static final String USER_FIELDS = "Id,Username,LastName,FirstName,Name,CompanyName,Division,Department,"
			+ "Title,Street,City,State,PostalCode,Country,Latitude,Longitude,"
			+ "Email,SenderEmail,SenderName,Signature,Phone,Fax,MobilePhone,Alias,"
			+ "CommunityNickname,IsActive,TimeZoneSidKey,LocaleSidKey,EmailEncodingKey,"
			+ "UserType,LanguageLocaleKey,EmployeeNumber,DelegatedApproverId,ManagerId,AboutMe";
	
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
	
	public Token getToken(String subject, String code) {
		Token token = null;
		
		try {
			HttpResponse httpResponse = RestResource.post(System.getProperty(Properties.SALESFORCE_TOKEN_URI))
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.contentType("application/x-www-form-urlencoded")
					.parameter("grant_type", "authorization_code")
					.parameter("code", code)
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
	
	public ServiceProviderInfo getAsServiceProvider(String subject, String code) {
		Token token = getToken(subject, code);
		
		Identity identity = getIdentity(token.getAccessToken(), token.getId());
		
		Organization organization = getOrganization(token.getAccessToken(), identity.getOrganizationId(), identity.getUrls().getSobjects());
		
		ServiceProviderInfo serviceProviderInfo = new ServiceProviderInfo();
		serviceProviderInfo.setAccount(identity.getUserId());
		serviceProviderInfo.setType("SALESFORCE");
		serviceProviderInfo.setInstanceId(organization.getId());
		serviceProviderInfo.setInstanceName(organization.getInstanceName());
		serviceProviderInfo.setInstanceUrl(token.getInstanceUrl());
		serviceProviderInfo.setIsSandbox(organization.getIsSandbox());
		serviceProviderInfo.setName(organization.getName());
		
		return serviceProviderInfo;
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
	
	public User getUser(String bearerToken, String userId, String sobjectUrl) {
		User user = null;
		
		try {
	     	
			HttpResponse httpResponse = RestResource.get(sobjectUrl)
	     			.bearerAuthorization(bearerToken)
	     			.path("User")
	     			.path(userId)
	     			.queryParameter("fields", USER_FIELDS)
	     			.queryParameter("version", "latest")
	     			.execute();
	     	
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " : " + httpResponse.getURL());
			
			if (httpResponse.getStatusCode() >= 400) {
				throw new WebApplicationException(httpResponse.getAsString(), httpResponse.getStatusCode());
			}
	     	
	     	user = httpResponse.getEntity(User.class);
	     	
		} catch (IOException e) {
			LOGGER.error( "getOrganization", e );
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
		
		return user;
	}
	
	/**
	 * 
	 * @param bearerToken
	 * @param organizationId
	 * @param sobjectUrl
	 * @return Organization
	 */
	
	public Organization getOrganization(String bearerToken, String organizationId, String sobjectUrl) {
		
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
	     	
			Organization organization = httpResponse.getEntity(Organization.class);
			
			return organization;
	     	
		} catch (IOException e) {
			LOGGER.error( "getOrganization", e );
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
	}
	
	public DescribeSObjectsResult describe(String subject, String userId) {
		
		Token token = hget( Token.class, subject, Token.class.getName().concat(userId) );
		
		try {
			
			HttpResponse httpResponse = RestResource.get(token.getInstanceUrl().concat("/services/data/v35.0/sobjects"))
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.execute();
			
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " : " + httpResponse.getURL());
			
			if (httpResponse.getStatusCode() >= 400) {
				throw new WebApplicationException(httpResponse.getAsString(), httpResponse.getStatusCode());
			}
			
			DescribeSObjectsResult result = httpResponse.getEntity(DescribeSObjectsResult.class);
			
			return result;
			
		} catch (IOException e) {
			LOGGER.error( "getSObjects", e );
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
	}
}