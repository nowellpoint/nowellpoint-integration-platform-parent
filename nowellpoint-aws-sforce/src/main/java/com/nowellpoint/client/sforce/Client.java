package com.nowellpoint.client.sforce;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nowellpoint.client.sforce.model.Count;
import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.DescribeResult;
import com.nowellpoint.client.sforce.model.Error;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.User;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class Client {
	
	private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	
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
	
	public DescribeGlobalResult describeGlobal(DescribeGlobalSobjectsRequest request) {
		HttpResponse httpResponse = RestResource.get(request.getSobjectsUrl())
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(request.getAccessToken())
				.execute();
		
		DescribeGlobalResult result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			result = httpResponse.getEntity(DescribeGlobalResult.class);
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
	
	public DescribeResult describeSobject(DescribeSobjectRequest request) {
		HttpResponse httpResponse = RestResource.get(request.getSobjectsUrl().concat(request.getSobject()).concat("/describe"))
				.header("If-Modified-Since", request.getIfModifiedSince() != null ? new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'").format(request.getIfModifiedSince()) : null)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(request.getAccessToken())
				.execute();
		
		DescribeResult result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			result = httpResponse.getEntity(DescribeResult.class);
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
	
	public Long getCount(CountRequest request) {
		HttpResponse httpResponse = RestResource.get(request.getQueryUrl())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(request.getAccessToken())
				.queryParameter("q", request.getQueryString())
				.execute();
		
		Long totalSize = Long.valueOf(0);
		
		if (httpResponse.getStatusCode() == Status.OK) {
			Count count = httpResponse.getEntity(Count.class);
			totalSize = count.getRecords().get(0).getExpr0();
		} else {
			LOGGER.warning(httpResponse.getAsString());
		}
			
		return totalSize;
	}
}