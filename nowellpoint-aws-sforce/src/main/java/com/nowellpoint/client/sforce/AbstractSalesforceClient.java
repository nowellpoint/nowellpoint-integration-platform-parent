package com.nowellpoint.client.sforce;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nowellpoint.client.sforce.model.Count;
import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.DescribeResult;
import com.nowellpoint.client.sforce.model.Error;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.User;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public abstract class AbstractSalesforceClient {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractSalesforceClient.class.getName());
	private static Map<String,Identity> IDENTITY_CACHE = new ConcurrentHashMap<String,Identity>();
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	private static final String USER_FIELDS = "Id,Username,LastName,FirstName,Name,CompanyName,Division,Department,"
			+ "Title,Street,City,State,PostalCode,Country,Latitude,Longitude,"
			+ "Email,SenderEmail,SenderName,Signature,Phone,Fax,MobilePhone,Alias,"
			+ "CommunityNickname,IsActive,TimeZoneSidKey,LocaleSidKey,EmailEncodingKey,"
			+ "UserType,LanguageLocaleKey,EmployeeNumber,DelegatedApproverId,ManagerId,AboutMe";
	
	private static final String ORGANIZATION_FIELDS = "Id,Division,Fax,DefaultLocaleSidKey,FiscalYearStartMonth,"
 			+ "InstanceName,IsSandbox,LanguageLocaleKey,Name,OrganizationType,Phone,PrimaryContact,"
 			+ "UsesStartDateAsFiscalYearName,Address";

	public Identity getIdentity(Token token) {
		Identity identity = null;
		
		if (IDENTITY_CACHE.containsKey(token.getId())) {
			LOGGER.info("cache entry found");
			identity = IDENTITY_CACHE.get(token.getId());
		} else {
			
			HttpResponse response = RestResource.get(token.getId())
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.queryParameter("version", "latest")
					.execute();
			
			if (response.getStatusCode() == Status.OK) {
				identity = response.getEntity(Identity.class);
			} else {
				throw new ClientException(response.getStatusCode(), response.getEntity(ArrayNode.class));
			}
			
			LOGGER.info("cache entry not found");
			
			IDENTITY_CACHE.put(token.getId(), identity);
		}
		
		return identity;
	}

	public DescribeGlobalResult describeGlobal(Token token) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.get(identity.getUrls().getSObjects())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.execute();
		
		DescribeGlobalResult describeGlobalResult = null;
		
		if (response.getStatusCode() == Status.OK) {
			describeGlobalResult = response.getEntity(DescribeGlobalResult.class);
		} else {
			throw new ClientException(response.getStatusCode(), response.getEntity(Error.class));
			//throw new ServiceException(response.getEntity(SalesforceApiError.class));
		}
		
		return describeGlobalResult;
	}
	
	public User getUser(Token token) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse httpResponse = RestResource.get(identity.getUrls().getSObjects())
     			.bearerAuthorization(token.getAccessToken())
     			.path("User")
     			.path(identity.getUserId())
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
	 * @param token
	 * @return the queried Organization
	 */
	
	public Organization getOrganization(Token token) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.get(identity.getUrls().getSObjects())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
     			.path("Organization")
     			.path(identity.getOrganizationId())
     			.queryParameter("fields", ORGANIZATION_FIELDS)
     			.queryParameter("version", "latest")
     			.execute();
		
		Organization organization = null;
		
		if (response.getStatusCode() == Status.OK) {
			organization = response.getEntity(Organization.class);
		} else {
			throw new ClientException(response.getStatusCode(), response.getEntity(Error.class));
		}
		
		return organization;
	}
	
	public DescribeResult describeSObject(Token token, String sobject) {
		return describeSObject(token, sobject, null);
	}
	
	/**
	 * 
	 * 
	 * @param request
	 * @return
	 * 
	 * 
	 */
	
	public DescribeResult describeSObject(Token token, String sobject, Date modifiedSince) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse httpResponse = RestResource.get(identity.getUrls().getSObjects().concat(sobject).concat("/describe"))
				.header("If-Modified-Since", modifiedSince != null ? new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'").format(modifiedSince) : null)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
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
	
	public Theme getTheme(Token token) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse httpResponse = RestResource.get(identity.getUrls().getRest().concat("theme"))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.execute();
		
		Theme result = null;
	
		if (httpResponse.getStatusCode() == Status.OK) {
			result = httpResponse.getEntity(Theme.class);
		} else {
			throw new ClientException(httpResponse.getStatusCode(), httpResponse.getEntity(ArrayNode.class));
		}
		
		return result;
	}
	
	public Long count(Token token, String query) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse httpResponse = RestResource.get(identity.getUrls().getQuery())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.queryParameter("q", query)
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
	
	public CreateResult createPushTopic(Token token, PushTopicRequest request) {
		
		Identity identity = getIdentity(token);
		
		String body = mapper.createObjectNode()
				.put("Name", request.getName())
				.put("Query", request.getQuery())
				.put("ApiVersion", request.getApiVersion())
				.put("NotifyForOperationCreate", request.getNotifyForOperationCreate())
				.put("NotifyForOperationUpdate", request.getNotifyForOperationUpdate())
				.put("NotifyForOperationUndelete", Boolean.TRUE)
				.put("NotifyForOperationDelete", Boolean.TRUE)
				.put("NotifyForFields", "All")
				.toString();
		
		HttpResponse response = RestResource.post(identity.getUrls().getSObjects().concat("PushTopic/"))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.body(body)
				.contentType(MediaType.APPLICATION_JSON)
                .execute();
		
		if (response.getStatusCode() == Status.CREATED) {
			return response.getEntity(CreateResult.class);
		} else {
			throw new ClientException(response.getStatusCode(), response.getEntity(ArrayNode.class));
		}
	}
	
	public void deletePushTopic(Token token, String topicId) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.delete(identity.getUrls().getSObjects().concat("PushTopic/").concat(topicId))
				.bearerAuthorization(token.getAccessToken())
				.execute();
		
		if (response.getStatusCode() != Status.NO_CONTENT) {
			throw new ClientException(response.getStatusCode(), response.getEntity(ArrayNode.class));
		}
	}
}