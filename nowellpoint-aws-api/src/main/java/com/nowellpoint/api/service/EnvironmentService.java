package com.nowellpoint.api.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.nowellpoint.util.Assert.isNull;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.ValidationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.DBRef;
import com.nowellpoint.api.model.domain.Environment;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.api.model.document.AccountProfile;
import com.nowellpoint.api.model.document.SObjectDescription;
import com.nowellpoint.api.model.document.UserRef;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.DescribeSobjectRequest;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.GetOrganizationRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.ThemeRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.LoginResult;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.client.sforce.model.sobject.Sobject;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.mongodb.document.MongoDatastore;

public class EnvironmentService {
	
	@Inject
	private SalesforceService salesforceService;
	
	/**
	 * 
	 * @param environment
	 */
	
	public void testConnection(Environment environment) {

		try {
			
			if (environment.getIsSandbox()) {
				
				String authEndpoint = environment.getAuthEndpoint();
				String username = environment.getUsername();
				String password = environment.getPassword();
				String securityToken = environment.getSecurityToken();
				
				LoginResult loginResult = salesforceService.login(authEndpoint, username, password, securityToken);		
				
				environment.setUserId(loginResult.getUserId());
				environment.setOrganizationId(loginResult.getOrganizationId());
				environment.setOrganizationName(loginResult.getOrganizationName());
				environment.setServiceEndpoint(loginResult.getServiceEndpoint());
				
			} else {
				
				String refreshToken = environment.getRefreshToken();
				
				OauthAuthenticationResponse authenticationResponse = salesforceService.refreshToken(refreshToken);
				
				Token token = authenticationResponse.getToken();
				Identity identity = authenticationResponse.getIdentity();
				
				Client client = new Client();

				GetOrganizationRequest getOrganizationRequest = new GetOrganizationRequest()
						.setAccessToken(token.getAccessToken())
						.setOrganizationId(identity.getOrganizationId())
						.setSobjectUrl(identity.getUrls().getSobjects());
				
				Organization organization = client.getOrganization(getOrganizationRequest);
				
				environment.setUserId(identity.getUserId());
				environment.setOrganizationId(identity.getOrganizationId());
				environment.setOrganizationName(organization.getName());
				environment.setServiceEndpoint(token.getInstanceUrl());
			}
			environment.setIsValid(Boolean.TRUE);
		} catch (OauthException e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getErrorDescription());
		} catch (ValidationException e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getMessage());
		} catch (Exception e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getMessage());
		}			
	}
	
	/**
	 * 
	 * @param environment
	 */
	
	public void buildEnvironment(Environment environment) {

		try {
			
			if (environment.getIsSandbox()) {
				
				String authEndpoint = environment.getAuthEndpoint();
				String username = environment.getUsername();
				String password = environment.getPassword();
				String securityToken = environment.getSecurityToken();
				
				LoginResult loginResult = salesforceService.login(authEndpoint, username, password, securityToken);		
				environment.setUserId(loginResult.getUserId());
				environment.setOrganizationId(loginResult.getOrganizationId());
				environment.setOrganizationName(loginResult.getOrganizationName());
				environment.setServiceEndpoint(loginResult.getServiceEndpoint());
				
				Client client = new Client();
				
				GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
						.setAccessToken(loginResult.getSessionId())
						.setId(loginResult.getId());
				
				Identity identity = client.getIdentity(getIdentityRequest);
				
				DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
						.setAccessToken(loginResult.getSessionId())
						.setSobjectsUrl(identity.getUrls().getSobjects());
				
				DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
				
				environment.setSobjects(describeGlobalSobjectsResult.getSobjects().stream().collect(Collectors.toSet()));
				
				ThemeRequest themeRequest = new ThemeRequest()
						.withAccessToken(loginResult.getSessionId())
						.withRestEndpoint(identity.getUrls().getRest());
				
				Theme theme = client.getTheme(themeRequest);
				
				environment.setTheme(theme);
				
				describeSobjects(loginResult.getSessionId(), identity.getUrls().getSobjects(), describeGlobalSobjectsResult, environment.getKey());
				
			} else {
				
				String refreshToken = environment.getRefreshToken();
				
				OauthAuthenticationResponse authenticationResponse = salesforceService.refreshToken(refreshToken);
				
				Token token = authenticationResponse.getToken();
				Identity identity = authenticationResponse.getIdentity();
				
				Client client = new Client();

				GetOrganizationRequest getOrganizationRequest = new GetOrganizationRequest()
						.setAccessToken(token.getAccessToken())
						.setOrganizationId(identity.getOrganizationId())
						.setSobjectUrl(identity.getUrls().getSobjects());
				
				Organization organization = client.getOrganization(getOrganizationRequest);
				
				environment.setUserId(identity.getUserId());
				environment.setOrganizationId(identity.getOrganizationId());
				environment.setOrganizationName(organization.getName());
				environment.setServiceEndpoint(token.getInstanceUrl());
				environment.setIsValid(Boolean.TRUE);
				
				DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
						.setAccessToken(token.getAccessToken())
						.setSobjectsUrl(identity.getUrls().getSobjects());
				
				DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
				
				environment.setSobjects(describeGlobalSobjectsResult.getSobjects().stream().collect(Collectors.toSet()));
				
				ThemeRequest themeRequest = new ThemeRequest()
						.withAccessToken(token.getAccessToken())
						.withRestEndpoint(identity.getUrls().getRest());
				
				Theme theme = client.getTheme(themeRequest);
				
				environment.setTheme(theme);
				
				describeSobjects(token.getAccessToken(), identity.getUrls().getSobjects(), describeGlobalSobjectsResult, environment.getKey());
			}
			environment.setIsValid(Boolean.TRUE);
		} catch (OauthException e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getErrorDescription());
		} catch (ValidationException e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getMessage());
		} catch (Exception e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getMessage());
		}	
	}
	
	private void describeSobjects(String accessToken, String sobjectsUrl, DescribeGlobalSobjectsResult describeGlobalSobjectsResult, String environmentKey) throws InterruptedException, ExecutionException, JsonProcessingException {
		ExecutorService executor = Executors.newFixedThreadPool(describeGlobalSobjectsResult.getSobjects().size());
		
		final Client client = new Client();
		
		for (Sobject sobject : describeGlobalSobjectsResult.getSobjects()) {
			executor.submit(() -> {
				DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
						.withAccessToken(accessToken)
						.withSobjectsUrl(sobjectsUrl)
						.withSobject(sobject.getName());

				DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);

				Date now = Date.from(Instant.now());
				//UserRef user = new UserRef(new DBRef(MongoDatastore.getCollectionName(AccountProfile.class), new ObjectId(UserContext.getSecurityContext().getUserPrincipal().getName())));

				SObjectDescription sobjectDescription = null;
				try {
					sobjectDescription = MongoDatastore.findOne(SObjectDescription.class, and ( eq ( "name", sobject.getName() ), eq ( "environmentKey", environmentKey )));
				} catch (DocumentNotFoundException e) {
					sobjectDescription = new SObjectDescription();
					sobjectDescription.setEnvironmentKey(environmentKey);
					sobjectDescription.setName(describeSobjectResult.getName());
					sobjectDescription.setCreatedDate(now);
					sobjectDescription.setSystemCreatedDate(now);
					sobjectDescription.setCreatedBy(null);
				}
				sobjectDescription.setLastModifiedBy(null);
				sobjectDescription.setLastModifiedDate(now);
				sobjectDescription.setSystemModifiedDate(now);
				sobjectDescription.setResult(describeSobjectResult);
				if (isNull(sobjectDescription.getId())) {
					MongoDatastore.insertOne(sobjectDescription);
				} else {
					MongoDatastore.replaceOne(sobjectDescription);
				}
			});
		}
		
		executor.shutdown();
		executor.awaitTermination(30, TimeUnit.SECONDS);
	}
}