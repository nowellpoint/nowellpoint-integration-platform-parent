package com.nowellpoint.console.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.nowellpoint.client.sforce.Salesforce;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.model.ApexClass;
import com.nowellpoint.client.sforce.model.ApexTrigger;
import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Limits;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Profile;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.Resources;
import com.nowellpoint.client.sforce.model.UserLicense;
import com.nowellpoint.client.sforce.model.UserRole;
import com.nowellpoint.client.sforce.model.QueryResult;
import com.nowellpoint.client.sforce.model.RecordType;
import com.nowellpoint.console.exception.ServiceException;
import com.nowellpoint.console.model.SalesforceApiError;
import com.nowellpoint.console.service.SalesforceService;
import com.nowellpoint.console.util.EnvironmentVariables;
import com.nowellpoint.console.util.SecretsManager;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class SalesforceServiceImpl implements SalesforceService {
	
	private static final Salesforce client = SalesforceClientBuilder.builder().build().getClient();
	
	@Override
	public Token getToken(String authorizationCode) {
		HttpResponse response = RestResource.get(EnvironmentVariables.getSalesforceTokenUri())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
                .queryParameter("grant_type", "authorization_code")
                .queryParameter("code", authorizationCode)
                .queryParameter("client_id", SecretsManager.getSalesforceClientId())
                .queryParameter("client_secret", SecretsManager.getSalesforceClientSecret())
                .queryParameter("redirect_uri", EnvironmentVariables.getSalesforceRedirectUri())
                .execute();
        
		Token token = null;
		
		if (response.getStatusCode() == Status.OK) {
			token = response.getEntity(Token.class);
		} else {
			throw new ServiceException(response.getEntity(SalesforceApiError.class));
		}
		
		return token;
	}
	
	@Override
	public Token refreshToken(String refreshToken) {
		HttpResponse response = RestResource.get(EnvironmentVariables.getSalesforceTokenUri())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
                .queryParameter("grant_type", "refresh_token")
                .queryParameter("refresh_token", refreshToken)
                .queryParameter("client_id", SecretsManager.getSalesforceClientId())
                .queryParameter("client_secret", SecretsManager.getSalesforceClientSecret())
                .execute();
		
		Token token = null;
		
		if (response.getStatusCode() == Status.OK) {
			token = response.getEntity(Token.class);
		} else {
			throw new ServiceException(response.getEntity(SalesforceApiError.class));
		}
		
		return token;
	}
	
	@Override
	public Identity getIdentity(Token token) {
		return client.getIdentity(token);
	}
	
	@Override
	public Organization getOrganization(Token token) {
		return client.getOrganization(token);
	}
	
	@Override
	public DescribeGlobalResult describeGlobal(Token token) {
		return client.describeGlobal(token);
	}
	
	@Override
	public Set<UserLicense> getUserLicenses(Token token) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.get(identity.getUrls().getQuery())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
     			.queryParameter("q", UserLicense.QUERY)
     			.execute();
		
		Set<UserLicense> userLicenses = Collections.emptySet();
		
		if (response.getStatusCode() == Status.OK) {
			QueryResult queryResult = response.getEntity(QueryResult.class);
			userLicenses = queryResult.getRecords(UserLicense.class);
		} else {
			throw new ServiceException(response.getEntity(SalesforceApiError.class));
		}
		
		return userLicenses;
		
	}
	
	@Override
	public Set<ApexClass> getApexClasses(Token token) {
	
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.get(identity.getUrls().getQuery())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
     			.queryParameter("q", ApexClass.QUERY)
     			.execute();
		
		Set<ApexClass> apexClasses = Collections.emptySet();
		
		if (response.getStatusCode() == Status.OK) {
			QueryResult queryResult = response.getEntity(QueryResult.class);
			apexClasses = queryResult.getRecords(ApexClass.class);
		} else {
			throw new ServiceException(response.getEntity(SalesforceApiError.class));
		}
		
		return apexClasses;
	}
	
	@Override
	public Set<ApexTrigger> getApexTriggers(Token token) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.get(identity.getUrls().getQuery())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
     			.queryParameter("q", ApexTrigger.QUERY)
     			.execute();
		
		Set<ApexTrigger> apexTriggers = Collections.emptySet();
		
		if (response.getStatusCode() == Status.OK) {
			QueryResult queryResult = response.getEntity(QueryResult.class);
			apexTriggers = queryResult.getRecords(ApexTrigger.class);
		} else {
			throw new ServiceException(response.getEntity(SalesforceApiError.class));
		}
		
		return apexTriggers;
	}
	
	@Override
	public Set<RecordType> getRecordTypes(Token token) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.get(identity.getUrls().getQuery())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
     			.queryParameter("q", RecordType.QUERY)
     			.execute();
		
		Set<RecordType> recordTypes = Collections.emptySet();
		
		if (response.getStatusCode() == Status.OK) {
			QueryResult queryResult = response.getEntity(QueryResult.class);
			recordTypes = queryResult.getRecords(RecordType.class);
		} else {
			throw new ServiceException(response.getEntity(SalesforceApiError.class));
		}
		
		return recordTypes;
	}
	
	@Override
	public Set<UserRole> getUserRoles(Token token) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.get(identity.getUrls().getQuery())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
     			.queryParameter("q", UserRole.QUERY)
     			.execute();
		
		Set<UserRole> userRoles = Collections.emptySet();
		
		if (response.getStatusCode() == Status.OK) {
			QueryResult queryResult = response.getEntity(QueryResult.class);
			userRoles = queryResult.getRecords(UserRole.class);
		} else {
			throw new ServiceException(response.getEntity(SalesforceApiError.class));
		}
		
		return userRoles;
	}
	
	@Override
	public Set<Profile> getProfiles(Token token) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.get(identity.getUrls().getQuery())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
     			.queryParameter("q", Profile.QUERY)
     			.execute();
		
		Set<Profile> profiles = Collections.emptySet();
		
		if (response.getStatusCode() == Status.OK) {
			QueryResult queryResult = response.getEntity(QueryResult.class);
			profiles = queryResult.getRecords(Profile.class);
		} else {
			List<SalesforceApiError> errors = response.getEntityList(SalesforceApiError.class);
			throw new ServiceException(errors.get(0));
		}
		
		return profiles;
	}
	
	@Override
	public Resources getResources(Token token) {
		
		HttpResponse response = RestResource.get(token.getInstanceUrl().concat("/services/data/v").concat(System.getenv("SALESFORCE_API_VERSION")))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.execute();
		
		return response.getEntity(Resources.class);
	}
	
	@Override
	public Limits getLimits(Token token) {
		
		Resources resources = getResources(token);
		
		HttpResponse response = RestResource.get(token.getInstanceUrl().concat(resources.getLimits()))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.execute();
		
		return response.getEntity(Limits.class);
	}
}