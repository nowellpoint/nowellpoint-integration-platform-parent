package com.nowellpoint.aws.api.service;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.api.dto.SalesforceConnectorDTO;
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
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.fault.LoginFault;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class SalesforceService {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SalesforceService.class);
	
	public SalesforceService() {

	}
	
	public DescribeSobjectsResult describe(String instance, String username, String password, String securityToken) {
		ConnectorConfig config = new ConnectorConfig();
		config.setAuthEndpoint(String.format("%s/services/Soap/u/36.0", instance));
		config.setUsername(username);
		config.setPassword(password.concat(securityToken));
		
		try {
			PartnerConnection connection = com.sforce.soap.partner.Connector.newConnection(config);
			
			String sessionId = connection.getConfig().getSessionId();
			String id = String.format("%s/id/%s/%s", instance, connection.getUserInfo().getOrganizationId(), connection.getUserInfo().getUserId());
			
			GetIdentityRequest request = new GetIdentityRequest()
					.setAccessToken(sessionId)
					.setId(id);
			
			Client client = new Client();
			
			Identity identity = client.getIdentity(request);
			
			return describe(sessionId, identity.getUrls().getSobjects());
			
		} catch (ConnectionException e) {
			if (e instanceof LoginFault) {
				LoginFault loginFault = (LoginFault) e;
				throw new BadRequestException(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
			} else {
				throw new InternalServerErrorException(e.getMessage());
			}
		}
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
	
	/**
	 * 
	 * @param accessToken
	 * @param userId
	 * @param sobjectUrl
	 * @return
	 */
	
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