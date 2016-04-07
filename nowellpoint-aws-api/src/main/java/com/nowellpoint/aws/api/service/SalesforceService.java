package com.nowellpoint.aws.api.service;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.aws.api.dto.sforce.ServiceInfo;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.AuthorizationGrantRequest;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeSobjectsRequest;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.GetOrganizationRequest;
import com.nowellpoint.client.sforce.GetUserRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.model.DescribeSobjectsResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.User;

public class SalesforceService {
	
	@SuppressWarnings("unused")
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
	
	public SalesforceConnectorDTO getSalesforceInstance(String accessToken, String id) {
		GetIdentityRequest request = new GetIdentityRequest()
				.setAccessToken(accessToken)
				.setId(id);
		
		Client client = new Client();
		
		Identity identity = client.getIdentity(request);
		
		Organization organization = getOrganization(accessToken, identity.getOrganizationId(), identity.getUrls().getSobjects());
		
		SalesforceConnectorDTO resource = new SalesforceConnectorDTO();
		resource.setOrganization(organization);
		resource.setIdentity(identity);
		
		return resource;
	}
	
	public ServiceInfo getServiceInfo(String accessToken, String id) {
		GetIdentityRequest request = new GetIdentityRequest()
				.setAccessToken(accessToken)
				.setId(id);
		
		Client client = new Client();
		
		Identity identity = client.getIdentity(request);
		
		Organization organization = getOrganization(accessToken, identity.getOrganizationId(), identity.getUrls().getSobjects());
		
		DescribeSobjectsResult result = describe(accessToken, identity.getUrls().getSobjects());
		
		ServiceInfo serviceInfo = new ServiceInfo();
		serviceInfo.setAccount(identity.getUserId());
		serviceInfo.setType("SALESFORCE");
		serviceInfo.setInstanceId(organization.getId());
		serviceInfo.setInstanceName(organization.getInstanceName());
		serviceInfo.setInstanceUrl(identity.getUrls().getPartner());
		serviceInfo.setIsSandbox(organization.getIsSandbox());
		serviceInfo.setName(organization.getName());
		serviceInfo.setSobjects(result.getSobjects());
				
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