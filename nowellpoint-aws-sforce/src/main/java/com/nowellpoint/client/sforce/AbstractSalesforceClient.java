package com.nowellpoint.client.sforce;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nowellpoint.client.sforce.model.ApexClass;
import com.nowellpoint.client.sforce.model.ApexTrigger;
import com.nowellpoint.client.sforce.model.Count;
import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.DescribeResult;
import com.nowellpoint.client.sforce.model.Error;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Limits;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Profile;
import com.nowellpoint.client.sforce.model.QueryResult;
import com.nowellpoint.client.sforce.model.RecordType;
import com.nowellpoint.client.sforce.model.Resources;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.User;
import com.nowellpoint.client.sforce.model.UserLicense;
import com.nowellpoint.client.sforce.model.UserRole;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public abstract class AbstractSalesforceClient {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractSalesforceClient.class.getName());
	private static Map<String,Identity> IDENTITY_CACHE = new ConcurrentHashMap<String,Identity>();
	
	private static String API_VERSION = "44.0";
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	private static final String USER_FIELDS = "Id,Username,LastName,FirstName,Name,CompanyName,Division,Department,"
			+ "Title,Street,City,State,PostalCode,Country,Latitude,Longitude,"
			+ "Email,SenderEmail,SenderName,Signature,Phone,Fax,MobilePhone,Alias,"
			+ "CommunityNickname,IsActive,TimeZoneSidKey,LocaleSidKey,EmailEncodingKey,"
			+ "UserType,LanguageLocaleKey,EmployeeNumber,DelegatedApproverId,ManagerId,AboutMe";
	
	private static final String ORGANIZATION_FIELDS = "Id,Division,Fax,DefaultLocaleSidKey,FiscalYearStartMonth,"
 			+ "InstanceName,IsSandbox,LanguageLocaleKey,Name,OrganizationType,Phone,PrimaryContact,"
 			+ "UsesStartDateAsFiscalYearName,Address";

	/**
     * Retrieves the <code>Identity</code> associated with the Token
     *
     * @param token the token returned from one of the authenticate methods.
     */
	
	protected Identity getIdentity(Token token) {
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

	/**
     * Describes and returns <code>DescribeGlobalResult</code> for the organization associated with the Token
     *
     * @param token the token returned from one of the authenticate methods.
     */
	
	protected DescribeGlobalResult describeGlobal(Token token) {
		
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
	
	/**
     * Retrieves and returns <code>User</code> for the user associated with the Token
     *
     * @param token the token returned from one of the authenticate methods.
     */
	
	protected User getUser(Token token) {
		
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
     * Retrieves and returns <code>Organization</code> for the organization associated with the Token
     *
     * @param token the token returned from one of the authenticate methods.
     */
	
	protected Organization getOrganization(Token token) {
		
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
	
	/**
     * Describes and returns <code>DescribeResult</code> for named sobject
     *
     * @param token the token returned from one of the authenticate methods.
     * @param sobject the name of the sobject to describe
     */
	
	protected DescribeResult describeSObject(Token token, String sobject) {
		return describeSObject(token, sobject, null);
	}
	
	/**
     * Describes and returns <code>DescribeResult</code> for named sobject modified since the date
     *
     * @param token the token returned from one of the authenticate methods.
     * @param sobject the name of the sobject to describe
     * @param modifiedSince returns objects if they have been modified since this date
     */
	
	protected DescribeResult describeSObject(Token token, String sobject, Date modifiedSince) {
		
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
	
	protected Theme getTheme(Token token) {
		
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
	
	protected Long count(Token token, String query) {
		
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
	
	protected CreateResult createPushTopic(Token token, PushTopicRequest request) {
		
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
	
	protected void deletePushTopic(Token token, String topicId) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.delete(identity.getUrls().getSObjects().concat("PushTopic/").concat(topicId))
				.bearerAuthorization(token.getAccessToken())
				.execute();
		
		if (response.getStatusCode() != Status.NO_CONTENT) {
			throw new ClientException(response.getStatusCode(), response.getEntity(ArrayNode.class));
		}
	}
	
	protected Set<UserLicense> getUserLicenses(Token token) {
		
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
			throw new ClientException(response.getStatusCode(), response.getEntity(ArrayNode.class));
		}
		
		return userLicenses;
		
	}
	
	protected Set<ApexClass> getApexClasses(Token token) {
		
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
			throw new ClientException(response.getStatusCode(), response.getEntity(ArrayNode.class));
		}
		
		return apexClasses;
	}
	
	protected Set<ApexTrigger> getApexTriggers(Token token) {
		
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
			throw new ClientException(response.getStatusCode(), response.getEntity(ArrayNode.class));
		}
		
		return apexTriggers;
	}
	
	protected Set<RecordType> getRecordTypes(Token token) {
		
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
			throw new ClientException(response.getStatusCode(), response.getEntity(ArrayNode.class));
		}
		
		return recordTypes;
	}
	
	protected Set<UserRole> getUserRoles(Token token) {
		
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
			throw new ClientException(response.getStatusCode(), response.getEntity(ArrayNode.class));
		}
		
		return userRoles;
	}
	
	protected Set<Profile> getProfiles(Token token) {
		
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
			throw new ClientException(response.getStatusCode(), response.getEntity(ArrayNode.class));
		}
		
		return profiles;
	}
	
	protected Resources getResources(Token token) {
		
		HttpResponse response = RestResource.get(token.getInstanceUrl().concat("/services/data/v").concat(API_VERSION))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.execute();
		
		return response.getEntity(Resources.class);
	}
	
	protected Limits getLimits(Token token) {
		
		Resources resources = getResources(token);
		
		HttpResponse response = RestResource.get(token.getInstanceUrl().concat(resources.getLimits()))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.execute();
		
		return response.getEntity(Limits.class);
	}
}