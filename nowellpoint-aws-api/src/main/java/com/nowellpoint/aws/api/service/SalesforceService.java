package com.nowellpoint.aws.api.service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.ws.rs.ForbiddenException;

import org.jboss.logging.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.esotericsoftware.yamlbeans.YamlReader;
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
import com.nowellpoint.client.sforce.model.LoginResult;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Package;
import com.nowellpoint.client.sforce.model.User;
import com.nowellpoint.client.sforce.model.Type;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.fault.LoginFault;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class SalesforceService extends AbstractCacheService {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SalesforceService.class);
	
	public SalesforceService() {

	}
	
	public LoginResult login(String instance, String username, String password, String securityToken) {
		ConnectorConfig config = new ConnectorConfig();
		config.setAuthEndpoint(String.format("%s/services/Soap/u/36.0", instance));
		config.setUsername(username);
		config.setPassword(password.concat(securityToken));
		
		try {
			PartnerConnection connection = com.sforce.soap.partner.Connector.newConnection(config);
			
			String id = String.format("%s/id/%s/%s", instance, connection.getUserInfo().getOrganizationId(), connection.getUserInfo().getUserId());
			
			LoginResult result = new LoginResult()
					.withId(id)
					.withAuthEndpoint(connection.getConfig().getAuthEndpoint())
					.withDisplayName(connection.getUserInfo().getUserFullName())
					.withOrganizationId(connection.getUserInfo().getOrganizationId())
					.withOrganziationName(connection.getUserInfo().getOrganizationName())
					.withServiceEndpoint(connection.getConfig().getServiceEndpoint())
					.withSessionId(connection.getConfig().getSessionId())
					.withUserId(connection.getUserInfo().getUserId())
					.withUserName(connection.getUserInfo().getUserName());
			
			set(id, result);
			
			return result;
			
		} catch (ConnectionException e) {
			if (e instanceof LoginFault) {
				LoginFault loginFault = (LoginFault) e;
				throw new BadRequestException(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
			} else {
				throw new InternalServerErrorException(e.getMessage());
			}
		}
	}
	
	public DescribeSobjectsResult describe(String id) {
		
		LoginResult result = get(LoginResult.class, id);
		
		if (result == null) {
			throw new ForbiddenException("Invalid id or Session has expired");
		}
			
		GetIdentityRequest request = new GetIdentityRequest()
				.setAccessToken(result.getSessionId())
				.setId(id);
			
		Client client = new Client();
			
		Identity identity = client.getIdentity(request);
			
		return describe(result.getSessionId(), identity.getUrls().getSobjects());
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
	
	@SuppressWarnings("unchecked")
	public void buildPackage(String key) {
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		GetObjectRequest getObjectRequest = new GetObjectRequest("nowellpoint-configuration-files", key);
    	
    	S3Object configFile = s3Client.getObject(getObjectRequest);
		
		try {
			StringReader sr = new StringReader(IOUtils.toString(configFile.getObjectContent()));
			
			YamlReader reader = new YamlReader(sr);
			
			Map<String,Object> configParams = (Map<String,Object>) reader.read();
			reader.close();
			
			String id = configParams.get("id").toString();
			String metadata = configParams.get("url.metadata").toString();
			List<String> sobjects = (ArrayList<String>) configParams.get("sobjects");
			
			System.out.println(id);
			
			LoginResult loginResult = get(LoginResult.class, id);
			
			if (loginResult == null) {
				throw new ForbiddenException("Invalid id or Session has expired");
			}
			
			System.out.println(loginResult.getSessionId());
				
			final ConnectorConfig config = new ConnectorConfig();
	        config.setServiceEndpoint(metadata);
	        System.out.println(metadata);
	        config.setSessionId(loginResult.getSessionId());
	        
	        MetadataConnection metadataConnection = new MetadataConnection(config);
			
			System.out.println(metadataConnection.getSessionHeader().getSessionId());
			
			String[][] artifacts = new String[][] {
				{"Outbound_Event__c","CustomObject"},
				{"Outbound_Event__c","Workflow"}
			};
			
			List<Type> types = new ArrayList<Type>();
			
			for (int i = 0; i < artifacts.length; i++) {
				Type type = new Type();
				type.setMembers(artifacts[i][0]);
				type.setName(artifacts[i][1]);
				types.add(type);
			}
			
			sobjects.stream().forEach(sobject -> {
				Type type = new Type();
				type.setMembers(String.format("%s_Event_Observer", sobject));
				type.setName("ApexTrigger");
				types.add(type);
			});
			
			
			Package manifest = new Package();
			manifest.setTypes(types);
			manifest.setVersion(36.0);
			
			StringWriter sw = new StringWriter();
			
			JAXBContext context = JAXBContext.newInstance( Package.class );
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
			marshaller.marshal( manifest, System.out );
			marshaller.marshal( manifest, sw );
	        
		} catch (IOException | ConnectionException | JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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