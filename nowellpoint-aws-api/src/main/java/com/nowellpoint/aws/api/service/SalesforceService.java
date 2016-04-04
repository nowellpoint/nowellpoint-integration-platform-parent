package com.nowellpoint.aws.api.service;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.api.dto.SalesforceInstanceDTO;
import com.nowellpoint.aws.api.dto.sforce.ServiceInfo;
import com.nowellpoint.aws.data.mongodb.SalesforceInstance;
import com.nowellpoint.aws.data.mongodb.sforce.Contact;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.AuthorizationGrantRequest;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeSobjectsRequest;
import com.nowellpoint.client.sforce.GetOrganizationRequest;
import com.nowellpoint.client.sforce.GetUserRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.model.DescribeSobjectsResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.User;

public class SalesforceService extends AbstractCacheService {
	
	private static final Logger LOGGER = Logger.getLogger(SalesforceService.class);
	
	public SalesforceService() {

	}
	
	/**
	 * 
	 * @param authCode
	 * @return
	 */
	
	public OauthAuthenticationResponse authenticate(String code) {		
		AuthorizationGrantRequest request = OauthRequests.AUTHORIZATION_GRANT_REQUEST
				.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setCallbackUri(System.getProperty(Properties.SALESFORCE_REDIRECT_URI))
				.setCode(code)
				.build();
		
		OauthAuthenticationResponse response = Authenticators.AUTHORIZATION_GRANT_AUTHENTICATOR
				.authenticate(request);
			
		return response;
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
	
	public SalesforceInstanceDTO getSalesforceInstance(String subject, String code) {
		OauthAuthenticationResponse response = authenticate(code);
		
		Token token = response.getToken();
		
		Identity identity = response.getIdentity();
		
		String userId = token.getId().substring(token.getId().lastIndexOf("/") + 1);
		
		hset( subject, Token.class.getName().concat( userId ), token);
		
		Organization organization = getOrganization(token.getAccessToken(), identity.getOrganizationId(), identity.getUrls().getSobjects());
		
		SalesforceInstanceDTO resource = new SalesforceInstanceDTO();
		resource.setOrganization(organization);
		resource.setIdentity(identity);
		
		return resource;
	}
	
	public ServiceInfo getServiceInfo(String subject, String code) {
		OauthAuthenticationResponse response = authenticate(code);
		
		Token token = response.getToken();
		
		Identity identity = response.getIdentity();
		
		String userId = token.getId().substring(token.getId().lastIndexOf("/") + 1);
		
		hset( subject, Token.class.getName().concat( userId ), token);
		
		Organization organization = getOrganization(token.getAccessToken(), identity.getOrganizationId(), identity.getUrls().getSobjects());
		
		DescribeSobjectsResult result = describe(token.getAccessToken(), identity.getUrls().getSobjects());
		
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
	
	public User getUser(String accessToken, String userId, String sobjectUrl) {	
		GetUserRequest request = new GetUserRequest()
				.setAccessToken(accessToken)
				.setSobjectUrl(sobjectUrl)
				.setUserId(userId);
		
		Client client = new Client();
		
		User user = client.getUser(request);

		return user;
	}
	
	/**
	 * 
	 * @param bearerToken
	 * @param organizationId
	 * @param sobjectUrl
	 * @return Organization
	 */
	
	public Organization getOrganization(String accessToken, String organizationId, String sobjectUrl) {		
		GetOrganizationRequest request = new GetOrganizationRequest()
				.setAccessToken(accessToken)
				.setOrganizationId(organizationId)
				.setSobjectUrl(sobjectUrl);
		
		Client client = new Client();
		
		Organization organization = client.getOrganization(request);
		
		return organization;
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	
	private DescribeSobjectsResult describe(String accessToken, String sobjectUrl) {
		DescribeSobjectsRequest request = new DescribeSobjectsRequest()
				.setAccessToken(accessToken)
				.setSobjectUrl(sobjectUrl);
		
		Client client = new Client();
		
		DescribeSobjectsResult result = client.describe(request);
		
		return result;
	}
}