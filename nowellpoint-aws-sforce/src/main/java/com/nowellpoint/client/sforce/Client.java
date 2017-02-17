package com.nowellpoint.client.sforce;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nowellpoint.client.sforce.model.Count;
import com.nowellpoint.client.sforce.model.Error;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.User;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class Client {
	
	private static final String USER_FIELDS = "Id,Username,LastName,FirstName,Name,CompanyName,Division,Department,"
			+ "Title,Street,City,State,PostalCode,Country,Latitude,Longitude,"
			+ "Email,SenderEmail,SenderName,Signature,Phone,Fax,MobilePhone,Alias,"
			+ "CommunityNickname,IsActive,TimeZoneSidKey,LocaleSidKey,EmailEncodingKey,"
			+ "UserType,LanguageLocaleKey,EmployeeNumber,DelegatedApproverId,ManagerId,AboutMe";
	
	private static final String ORGANIZATION_FIELDS = "Id,Division,Fax,DefaultLocaleSidKey,FiscalYearStartMonth,"
 			+ "InstanceName,IsSandbox,LanguageLocaleKey,Name,OrganizationType,Phone,PrimaryContact,"
 			+ "UsesStartDateAsFiscalYearName";
	
	public Client() {
		
	}
	
	/**
	 * 
	 * 
	 * @param request
	 * @return
	 * 
	 * 
	 */
	
	public Identity getIdentity(GetIdentityRequest request) {
		String id = null;
		if (request.getId() == null) {
			id = String.format("%s/id/%s/%s", request.getInstance(), request.getOrganizationId(), request.getUserId());
		} else {
			id = request.getId();
		}
		
		HttpResponse httpResponse = RestResource.get(id)
				.acceptCharset(StandardCharsets.UTF_8)
				.bearerAuthorization(request.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.queryParameter("version", "latest")
				.execute();
    	
		Identity identity = null;
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		identity = httpResponse.getEntity(Identity.class);
		} else {
			throw new ClientException(httpResponse.getStatusCode(), httpResponse.getEntity(Error.class));
		}
    	
    	return identity;
	}
	
	/**
	 * 
	 * 
	 * @param request
	 * @return
	 * 
	 * 
	 */
	
	public User getUser(GetUserRequest request) {
		HttpResponse httpResponse = RestResource.get(request.getSobjectUrl())
     			.bearerAuthorization(request.getAccessToken())
     			.path("User")
     			.path(request.getUserId())
     			.queryParameter("fields", USER_FIELDS)
     			.queryParameter("version", "latest")
     			.execute();
		
		User user = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			user = httpResponse.getEntity(User.class);
		} else {
			throw new ClientException(httpResponse.getStatusCode(), httpResponse.getEntity(Error.class));
		}
		
		return user;
	}
	
	/**
	 * 
	 * 
	 * @param request
	 * @return
	 * 
	 * 
	 */
	
	public Organization getOrganization(GetOrganizationRequest request) {
		HttpResponse httpResponse = RestResource.get(request.getSobjectUrl())
     			.bearerAuthorization(request.getAccessToken())
     			.path("Organization")
     			.path(request.getOrganizationId())
     			.queryParameter("fields", ORGANIZATION_FIELDS)
     			.queryParameter("version", "latest")
     			.execute();
		
		Organization organization = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			organization = httpResponse.getEntity(Organization.class);
		} else {
			throw new ClientException(httpResponse.getStatusCode(), httpResponse.getEntity(Error.class));
		}
		
		return organization;
	}
	
	/**
	 * 
	 * 
	 * @param request
	 * @return
	 * 
	 * 
	 */
	
	public DescribeGlobalSobjectsResult describeGlobal(DescribeGlobalSobjectsRequest request) {
		HttpResponse httpResponse = RestResource.get(request.getSobjectsUrl())
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(request.getAccessToken())
				.execute();
		
		DescribeGlobalSobjectsResult result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			result = httpResponse.getEntity(DescribeGlobalSobjectsResult.class);
		} else {
			throw new ClientException(httpResponse.getStatusCode(), httpResponse.getEntity(ArrayNode.class));
		}
		
		return result;
	}
	
	/**
	 * 
	 * 
	 * @param request
	 * @return
	 * 
	 * 
	 */
	
	public DescribeSobjectResult describeSobject(DescribeSobjectRequest request) {
		HttpResponse httpResponse = RestResource.get(request.getSobjectsUrl().concat(request.getSobject()).concat("/describe"))
				.header("If-Modified-Since", request.getIfModifiedSince() != null ? new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'").format(request.getIfModifiedSince()) : null)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(request.getAccessToken())
				.execute();
		
		DescribeSobjectResult result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			result = httpResponse.getEntity(DescribeSobjectResult.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_MODIFIED) {
			return null;
		} else {
			throw new ClientException(httpResponse.getStatusCode(), httpResponse.getEntity(ArrayNode.class));
		}
		
		return result;
	}
	
	/**
	 * 
	 * 
	 * @param request
	 * @return
	 * 
	 * 
	 */
	
	public Theme getTheme(ThemeRequest request) {
		HttpResponse httpResponse = RestResource.get(request.getRestEndpoint().concat("theme"))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(request.getAccessToken())
				.execute();
		
		Theme result = null;
	
		if (httpResponse.getStatusCode() == Status.OK) {
			result = httpResponse.getEntity(Theme.class);
		} else {
			throw new ClientException(httpResponse.getStatusCode(), httpResponse.getEntity(ArrayNode.class));
		}
		
		return result;
	}
	
	/**
	 * 
	 * 
	 * @param request
	 * @return
	 * 
	 * 
	 */
	
	public Count getCount(CountRequest request) {
		HttpResponse httpResponse = RestResource.get(request.getQueryUrl())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(request.getAccessToken())
				.queryParameter("q", request.getQueryString())
				.execute();
		
		Count result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			result = httpResponse.getEntity(Count.class);
		} else {
			throw new ClientException(httpResponse.getStatusCode(), httpResponse.getEntity(ArrayNode.class));
		}
			
		return result;
	}
}