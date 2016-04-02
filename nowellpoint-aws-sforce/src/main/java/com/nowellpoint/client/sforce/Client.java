package com.nowellpoint.client.sforce;

import java.io.IOException;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.sforce.model.DescribeSobjectsResult;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.User;

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
	 * @param request
	 * @return
	 */
	
	public User getUser(GetUserRequest request) {
		User user = null;
		
		try {
	     	
			HttpResponse httpResponse = RestResource.get(request.getSobjectUrl())
	     			.bearerAuthorization(request.getAccessToken())
	     			.path("User")
	     			.path(request.getUserId())
	     			.queryParameter("fields", USER_FIELDS)
	     			.queryParameter("version", "latest")
	     			.execute();
			
			if (httpResponse.getStatusCode() >= Status.OK) {
				user = httpResponse.getEntity(User.class);
			} else {
				
			}
	     	
		} catch (IOException e) {

		}
		
		return user;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	
	public Organization getOrganization(GetOrganizationRequest request) {
		Organization organization = null;
		
		try {
	     	
			HttpResponse httpResponse = RestResource.get(request.getSobjectUrl())
	     			.bearerAuthorization(request.getAccessToken())
	     			.path("Organization")
	     			.path(request.getOrganizationId())
	     			.queryParameter("fields", ORGANIZATION_FIELDS)
	     			.queryParameter("version", "latest")
	     			.execute();
			
			if (httpResponse.getStatusCode() >= 400) {
				
			}
	     	
			organization = httpResponse.getEntity(Organization.class);
			
			return organization;
	     	
		} catch (IOException e) {
			
		}
		
		return organization;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	
	public DescribeSobjectsResult describe(DescribeSobjectsRequest request) {
		DescribeSobjectsResult result = null;
		
		try {
			HttpResponse httpResponse = RestResource.get(request.getSobjectUrl())
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(request.getAccessToken())
					.execute();
			
			if (httpResponse.getStatusCode() >= 400) {
				
			}
			
			result = httpResponse.getEntity(DescribeSobjectsResult.class);
			
		} catch (IOException e) {

		}
		
		return result;
	}
}