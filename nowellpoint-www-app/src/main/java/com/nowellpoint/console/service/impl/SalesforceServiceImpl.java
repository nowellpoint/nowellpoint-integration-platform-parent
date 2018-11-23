package com.nowellpoint.console.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;

import com.nowellpoint.client.sforce.model.ApexClass;
import com.nowellpoint.client.sforce.model.ApexTrigger;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.UserLicense;
import com.nowellpoint.client.sforce.model.QueryResult;
import com.nowellpoint.client.sforce.model.RecordType;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalResult;
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
		HttpResponse response = RestResource.get(token.getId())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.queryParameter("version", "latest")
				.execute();
		
		Identity identity = null;
		
		if (response.getStatusCode() == Status.OK) {
			identity = response.getEntity(Identity.class);
		} else {
			throw new ServiceException(response.getEntity(SalesforceApiError.class));
		}
		
		return identity;
	}
	
	@Override
	public Organization getOrganization(Token token) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.get(identity.getUrls().getSobjects())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
     			.path("Organization")
     			.path(identity.getOrganizationId())
     			.queryParameter("fields", "Id,Name,Address")
     			.queryParameter("version", "latest")
     			.execute();
		
		Organization organization = null;
		
		if (response.getStatusCode() == Status.OK) {
			organization = response.getEntity(Organization.class);
		} else {
			throw new ServiceException(response.getEntity(SalesforceApiError.class));
		}
		
		return organization;
	}
	
	@Override
	public DescribeGlobalResult describeGlobal(Token token) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.get(identity.getUrls().getSobjects())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.execute();
		
		DescribeGlobalResult describeGlobalResult = null;
		
		if (response.getStatusCode() == Status.OK) {
			describeGlobalResult = response.getEntity(DescribeGlobalResult.class);
		} else {
			throw new ServiceException(response.getEntity(SalesforceApiError.class));
		}
		
		return describeGlobalResult;
	}
	
	@Override
	public Set<UserLicense> getUserLicenses(Token token) {
		
		String query = "Select "
				+ "CreatedDate, "
				+ "Id, "
				+ "LastModifiedDate, "
				+ "LicenseDefinitionKey, "
				+ "MasterLabel, "
				+ "Name, "
				+ "Status, "
				+ "TotalLicenses, "
				+ "UsedLicenses, "
				+ "UsedLicensesLastUpdated "
				+ "From UserLicense";
		
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.get(identity.getUrls().getQuery())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
     			.queryParameter("q", query)
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
}