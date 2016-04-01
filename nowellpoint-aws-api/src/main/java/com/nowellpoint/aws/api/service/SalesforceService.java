package com.nowellpoint.aws.api.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.api.dto.sforce.DescribeSObjectsResult;
import com.nowellpoint.aws.api.dto.sforce.ServiceInfo;
import com.nowellpoint.aws.api.exception.ServiceException;
import com.nowellpoint.aws.data.mongodb.SalesforceInstance;
import com.nowellpoint.aws.data.mongodb.sforce.Contact;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.AuthorizationGrantRequest;
import com.nowellpoint.client.sforce.OauthAuthorizationGrantResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.User;

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
	
	public Token getToken(String subject, String code) throws ServiceException {
		
		AuthorizationGrantRequest request = OauthRequests.AUTHORIZATION_GRANT_REQUEST
				.builder()
				.setCode(code)
				.build();
		
		OauthAuthorizationGrantResponse response = Authenticators.AUTHORIZATION_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		Token token = response.getToken();
		
		String userId = token.getId().substring(token.getId().lastIndexOf("/") + 1);
		
		hset( subject, Token.class.getName().concat( userId ), token);
		
//		try {
//			HttpResponse httpResponse = RestResource.post(System.getProperty(Properties.SALESFORCE_TOKEN_URI))
//					.acceptCharset(StandardCharsets.UTF_8)
//					.accept(MediaType.APPLICATION_JSON)
//					.contentType("application/x-www-form-urlencoded")
//					.parameter("grant_type", "authorization_code")
//					.parameter("code", code)
//					.parameter("client_id", System.getProperty(Properties.SALESFORCE_CLIENT_ID))
//					.parameter("client_secret", System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
//					.parameter("redirect_uri", System.getProperty(Properties.SALESFORCE_REDIRECT_URI))
//					.execute();
//			
//			LOGGER.info("Token response status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
//			
//			if (httpResponse.getStatusCode() >= 400) {
//				throw new ServiceException(httpResponse.getAsString(), httpResponse.getStatusCode());
//			}
//			
//			token = httpResponse.getEntity(Token.class);
//			
//			String userId = token.getId().substring(token.getId().lastIndexOf("/") + 1);
//			
//			LOGGER.info("Salesforce UserId authenticate: " + userId);
//			
//			hset( subject, Token.class.getName().concat( userId ), token);
//			//expire( subject, 3600 );
//			
//		} catch (IOException e) {
//			LOGGER.error( "getIdentity", e.getCause() );
//			throw new WebApplicationException(e, Status.BAD_REQUEST);
//		}
			
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
	
	public ServiceInfo getServiceInfo(String subject, String code) throws ServiceException {
		Token token = getToken(subject, code);
		
		Identity identity = getIdentity(token.getAccessToken(), token.getId());
		
		Organization organization = getOrganization(token.getAccessToken(), identity.getOrganizationId(), identity.getUrls().getSobjects());
		
		DescribeSObjectsResult result = describe(subject, identity.getUserId());
		
		ServiceInfo serviceInfo = new ServiceInfo();
		serviceInfo.setAccount(identity.getUserId());
		serviceInfo.setType("SALESFORCE");
		serviceInfo.setInstanceId(organization.getId());
		serviceInfo.setInstanceName(organization.getInstanceName());
		serviceInfo.setInstanceUrl(token.getInstanceUrl());
		serviceInfo.setIsSandbox(organization.getIsSandbox());
		serviceInfo.setName(organization.getName());
		serviceInfo.setSobjects(result.getSobjects());
		
		ObjectNode json = new ObjectMapper().createObjectNode()
				.put("instanceUrl", token.getInstanceUrl())
				.objectNode()
				.putObject("organization")
				.put("id", organization.getId())
				.put("defaultLocaleSidKey", organization.getDefaultLocaleSidKey())
				.put("division", organization.getDivision())
				.put("fax", organization.getFax());
		
		LOGGER.info(json.toString());

		
		SalesforceInstance salesforceInstance = new SalesforceInstance();
		salesforceInstance.setDefaultLocaleSidKey(organization.getDefaultLocaleSidKey());
		salesforceInstance.setDivision(organization.getDivision());
		salesforceInstance.setFax(organization.getFax());
		salesforceInstance.setFiscalYearStartMonth(organization.getFiscalYearStartMonth());
		salesforceInstance.setInstanceName(organization.getInstanceName());
		salesforceInstance.setLanguageLocaleKey(organization.getLanguageLocaleKey());
		salesforceInstance.setName(organization.getName());
		salesforceInstance.setOrganizationType(organization.getOrganizationType());
		salesforceInstance.setPhone(organization.getPhone());
		salesforceInstance.setPrimaryContact(organization.getPrimaryContact());
		
		Contact contact = new Contact();
		contact.setActive(identity.getActive());
		contact.setCity(identity.getAddrCity());
		contact.setCountry(identity.getAddrCountry());
		contact.setDisplayName(identity.getDisplayName());
		contact.setEmail(identity.getEmail());
		contact.setFirstName(identity.getFirstName());
		contact.setLanguage(identity.getLanguage());
		contact.setLastName(identity.getLastName());
		contact.setLocale(identity.getLocale());
		contact.setMobilePhone(identity.getMobilePhone());
		contact.setNickName(identity.getNickName());
		contact.setState(identity.getAddrState());
		contact.setStreet(identity.getAddrStreet());
		contact.setUsername(identity.getUsername());
		contact.setZipPostalCode(identity.getAddrZip());
		
		salesforceInstance.addContact(contact);
		
		return serviceInfo;
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
			LOGGER.error( "describe", e );
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
	}
	
	public void describeSObject(String subject, String userId) {
		
		Token token = hget( Token.class, subject, Token.class.getName().concat(userId) );
		
		///vXX.X/sobjects/SObjectName/describe/
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.get(token.getInstanceUrl().concat("/services/data/v35.0/sobjects"))
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.execute();
		} catch (IOException e) {
			LOGGER.error( "describeSObject", e );
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " : " + httpResponse.getURL());
		
	}
	
	public void createOrUpdateTrigger() {
		

		
		
	}
}