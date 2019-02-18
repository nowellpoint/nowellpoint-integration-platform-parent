package com.nowellpoint.client.sforce;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nowellpoint.client.sforce.model.ApexClass;
import com.nowellpoint.client.sforce.model.ApexTrigger;
import com.nowellpoint.client.sforce.model.CreateResult;
import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.DescribeResult;
import com.nowellpoint.client.sforce.model.Error;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Limits;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Profile;
import com.nowellpoint.client.sforce.model.PushTopic;
import com.nowellpoint.client.sforce.model.PushTopicRequest;
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

import lombok.Value;

@Value
final class SalesforceClient implements Salesforce {

	private static final Logger LOGGER = Logger.getLogger(SalesforceClient.class.getName());
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	private static final String USER_FIELDS = "Id,Username,LastName,FirstName,Name,CompanyName,Division,Department,"
			+ "Title,Street,City,State,PostalCode,Country,Latitude,Longitude,"
			+ "Email,SenderEmail,SenderName,Signature,Phone,Fax,MobilePhone,Alias,"
			+ "CommunityNickname,IsActive,TimeZoneSidKey,LocaleSidKey,EmailEncodingKey,"
			+ "UserType,LanguageLocaleKey,EmployeeNumber,DelegatedApproverId,ManagerId,AboutMe";
	
	private static final String ORGANIZATION_FIELDS = "Id,Division,Fax,DefaultLocaleSidKey,FiscalYearStartMonth,"
 			+ "InstanceName,IsSandbox,LanguageLocaleKey,Name,OrganizationType,Phone,PrimaryContact,"
 			+ "UsesStartDateAsFiscalYearName,Address";
	
	private static final String PUSH_TOPIC_FIELDS = "Id,ApiVersion,Description,IsActive,IsDeleted,CreatedDate,"
			+ "LastModifiedById,LastModifiedDate,Name,NotifyForFields,NotifyForOperationCreate,NotifyForOperationDelete,"
			+ "NotifyForOperations,NotifyForOperationUndelete,NotifyForOperationUpdate,Query";
	
	private Token token;
	private Identity identity;
	
	/**
	 * 
	 * @param token
	 */
	
	public SalesforceClient(Token token) {
		this.token = token;
		this.identity = queryIdentity();
	}

	/**
     * Retrieves the <code>Identity</code> associated with the Token
     *
     * @param token the token returned from one of the authenticate methods.
     */
	
	@Override
	public Identity getIdentity() {
		return identity;
	}

	/**
     * Describes and returns <code>DescribeGlobalResult</code> for the organization associated with the Token
     *
     * @param token the token returned from one of the authenticate methods.
     */
	
	@Override
	public DescribeGlobalResult describeGlobal() {
		
		Identity identity = getIdentity();
		
		HttpResponse response = RestResource.get(identity.getUrls().getSObjects())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(getToken().getAccessToken())
				.execute();
		
		DescribeGlobalResult describeGlobalResult = null;
		
		if (response.getStatusCode() == Status.OK) {
			describeGlobalResult = response.getEntity(DescribeGlobalResult.class);
		} else {
			throw new SalesforceClientException(response.getStatusCode(), response.getEntity(Error.class));
		}
		
		return describeGlobalResult;
	}
	
	/**
     * Retrieves and returns <code>User</code> for the user associated with the Token
     *
     * @param token the token returned from one of the authenticate methods.
     */
	
	@Override
	public User getUser() {
		
		Identity identity = getIdentity();
		
		HttpResponse httpResponse = RestResource.get(identity.getUrls().getSObjects())
     			.bearerAuthorization(getToken().getAccessToken())
     			.path("User")
     			.path(identity.getUserId())
     			.queryParameter("fields", USER_FIELDS)
     			.queryParameter("version", "latest")
     			.execute();
		
		User user = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			user = httpResponse.getEntity(User.class);
		} else {
			throw new SalesforceClientException(httpResponse.getStatusCode(), httpResponse.getEntity(Error.class));
		}
		
		return user;
	}
	
	/**
     * Retrieves and returns <code>Organization</code> for the organization associated with the Token
     *
     * @param token the token returned from one of the authenticate methods.
     */
	
	@Override
	public Organization getOrganization() {
		
		Identity identity = getIdentity();
		
		HttpResponse response = RestResource.get(identity.getUrls().getSObjects())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(getToken().getAccessToken())
     			.path("Organization")
     			.path(identity.getOrganizationId())
     			.queryParameter("fields", ORGANIZATION_FIELDS)
     			.queryParameter("version", "latest")
     			.execute();
		
		Organization organization = null;
		
		if (response.getStatusCode() == Status.OK) {
			organization = response.getEntity(Organization.class);
		} else {
			throw new SalesforceClientException(response.getStatusCode(), response.getEntity(Error.class));
		}
		
		return organization;
	}
	
	/**
     * Describes and returns <code>DescribeResult</code> for named sobject
     *
     * @param token the token returned from one of the authenticate methods.
     * @param sobject the name of the sobject to describe
     */
	
	@Override
	public DescribeResult describeSObject(String sobject) {
		return describeSObject(sobject, null);
	}
	
	/**
     * Describes and returns <code>DescribeResult</code> for named sobject modified since the date
     *
     * @param token the token returned from one of the authenticate methods.
     * @param sobject the name of the sobject to describe
     * @param modifiedSince returns objects if they have been modified since this date
     */
	
	@Override
	public DescribeResult describeSObject(String sobject, Date modifiedSince) {
		
		Identity identity = getIdentity();
		
		HttpResponse httpResponse = RestResource.get(identity.getUrls().getSObjects().concat(sobject).concat("/describe"))
				.header("If-Modified-Since", modifiedSince != null ? new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'").format(modifiedSince) : null)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(getToken().getAccessToken())
				.execute();
		
		DescribeResult result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			result = httpResponse.getEntity(DescribeResult.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_MODIFIED) {
			return null;
		} else {
			throw new SalesforceClientException(httpResponse.getStatusCode(), httpResponse.getEntity(ArrayNode.class));
		}
		
		return result;
	}
	
	@Override
	public Theme getTheme() {
		
		Identity identity = getIdentity();
		
		HttpResponse httpResponse = RestResource.get(identity.getUrls().getRest().concat("theme"))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(getToken().getAccessToken())
				.execute();
		
		Theme result = null;
	
		if (httpResponse.getStatusCode() == Status.OK) {
			result = httpResponse.getEntity(Theme.class);
		} else {
			throw new SalesforceClientException(httpResponse.getStatusCode(), httpResponse.getEntity(ArrayNode.class));
		}
		
		return result;
	}
	
	@Override
	public Long count(String query) {
		
		Identity identity = getIdentity();
		
		HttpResponse httpResponse = RestResource.get(identity.getUrls().getQuery())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(getToken().getAccessToken())
				.queryParameter("q", query)
				.execute();
		
		Long totalSize = Long.valueOf(0);
		
		if (httpResponse.getStatusCode() == Status.OK) {
			QueryResult count = httpResponse.getEntity(QueryResult.class);
			totalSize = count.getTotalSize();
		} else {
			LOGGER.warning(httpResponse.getAsString());
		}
			
		return totalSize;
	}
	
	@Override
	public CreateResult createPushTopic(PushTopicRequest request) {
		
		Identity identity = getIdentity();
		
		String body = mapper.createObjectNode()
				.put("Name", request.getName())
				.put("Query", request.getQuery())
				.put("ApiVersion", request.getApiVersion())
				.put("NotifyForOperationCreate", request.getNotifyForOperationCreate())
				.put("NotifyForOperationUpdate", request.getNotifyForOperationUpdate())
				.put("NotifyForOperationUndelete", request.getNotifyForOperationUndelete())
				.put("NotifyForOperationDelete", request.getNotifyForOperationDelete())
				.put("IsActive", request.getIsActive())
				.put("Description", request.getDescription())
				.put("NotifyForFields", request.getNotifyForFields())
				.toString();
		
		HttpResponse response = RestResource.post(identity.getUrls().getSObjects().concat("PushTopic/"))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(getToken().getAccessToken())
				.body(body)
				.contentType(MediaType.APPLICATION_JSON)
                .execute();
		
		if (response.getStatusCode() == Status.CREATED) {
			return response.getEntity(CreateResult.class);
		} else {
			//[{"message":"Topic Name: data value too large: NOWELLPOINT_OPPORTUNITY_PUSH_TOPIC (max length=25)","errorCode":"STRING_TOO_LONG","fields":["Name"]}]
			throw new SalesforceClientException(response.getStatusCode(), response.getEntityList(ApiError.class).get(0));
		}
	}
	
	@Override
	public void updatePushTopic(String topicId, PushTopicRequest request) {
		
		Identity identity = getIdentity();
		
		String body = mapper.createObjectNode()
				.put("Name", request.getName())
				.put("Query", request.getQuery())
				.put("ApiVersion", request.getApiVersion())
				.put("NotifyForOperationCreate", request.getNotifyForOperationCreate())
				.put("NotifyForOperationUpdate", request.getNotifyForOperationUpdate())
				.put("NotifyForOperationUndelete", request.getNotifyForOperationUndelete())
				.put("NotifyForOperationDelete", request.getNotifyForOperationDelete())
				.put("IsActive", request.getIsActive())
				.put("NotifyForFields", "All")
				.toString();
		
		HttpResponse response = RestResource.post(identity.getUrls().getSObjects().concat("PushTopic/").concat(topicId).concat("?_HttpMethod=PATCH"))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(getToken().getAccessToken())
				.body(body)
				.contentType(MediaType.APPLICATION_JSON)
                .execute();
		
		if (response.getStatusCode() != Status.NO_CONTENT) {
			throw new SalesforceClientException(response.getStatusCode(), response.getEntityList(ApiError.class).get(0));
		}
	}
	
	@Override
	public PushTopic getPushTopic(String pushTopicId) {
		
		Identity identity = getIdentity();
		
		HttpResponse response = RestResource.get(identity.getUrls().getSObjects())
     			.bearerAuthorization(getToken().getAccessToken())
     			.path("PushTopic")
     			.path(pushTopicId)
     			.queryParameter("fields", PUSH_TOPIC_FIELDS)
     			.queryParameter("version", "latest")
     			.execute();
		
		PushTopic pushTopic = null;
		
		if (response.getStatusCode() == Status.OK) {
			pushTopic = response.getEntity(PushTopic.class);
		} else {
			throw new SalesforceClientException(response.getStatusCode(), response.getEntity(ApiError.class));
		}
		
		return pushTopic;
	}
	
	@Override
	public void deletePushTopic(String topicId) {
		
		Identity identity = getIdentity();
		
		HttpResponse response = RestResource.delete(identity.getUrls().getSObjects().concat("PushTopic/").concat(topicId))
				.bearerAuthorization(getToken().getAccessToken())
				.execute();
		
		if (response.getStatusCode() != Status.NO_CONTENT) {
			throw new SalesforceClientException(response.getStatusCode(), response.getEntity(ArrayNode.class));
		}
	}
	
	@Override
	public Set<UserLicense> getUserLicenses() {
		return query(UserLicense.class, UserLicense.QUERY);
	}
	
	@Override
	public Set<ApexClass> getApexClasses() {
		return query(ApexClass.class, ApexClass.QUERY);
	}
	
	@Override
	public Set<ApexTrigger> getApexTriggers() {
		return query(ApexTrigger.class, ApexTrigger.QUERY);
	}
	
	@Override
	public Set<RecordType> getRecordTypes() {
		return query(RecordType.class, RecordType.QUERY);
	}
	
	@Override
	public Set<UserRole> getUserRoles() {
		return query(UserRole.class, UserRole.QUERY);
	}
	
	@Override
	public Set<Profile> getProfiles() {
		return query(Profile.class, Profile.QUERY);
	}
	
	@Override
	public Resources getResources() {
		
		HttpResponse response = RestResource.get(getToken().getInstanceUrl().concat("/services/data/v").concat(API_VERSION))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(getToken().getAccessToken())
				.execute();
		
		return response.getEntity(Resources.class);
	}
	
	@Override
	public Limits getLimits() {
		
		Resources resources = getResources();
		
		HttpResponse response = RestResource.get(getToken().getInstanceUrl().concat(resources.getLimits()))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(getToken().getAccessToken())
				.execute();
		
		return response.getEntity(Limits.class);
	}
	
	@Override
	public <T> Set<T> query(Class<T> type, String query) {
		
		Identity identity = getIdentity();
		
		HttpResponse response = RestResource.get(identity.getUrls().getQuery())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(getToken().getAccessToken())
     			.queryParameter("q", query)
     			.execute();
		
		Set<T> records = Collections.emptySet();
		
		if (response.getStatusCode() == Status.OK) {
			QueryResult queryResult = response.getEntity(QueryResult.class);
			records = queryResult.getRecords(type);
			while(! queryResult.getDone()) {
				queryResult = queryMore(queryResult.getNextRecordsUrl());
				records.addAll(queryResult.getRecords(type));
			}
			
		} else {
			throw new SalesforceClientException(response.getStatusCode(), response.getEntityList(ApiError.class).get(0));
		}
		
		return records;
	}
	
	private QueryResult queryMore(String nextRecordsUrl) {
		
		HttpResponse response = RestResource.get(getToken().getInstanceUrl().concat(nextRecordsUrl))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(getToken().getAccessToken())
     			.execute();
		
		if (response.getStatusCode() == Status.OK) {
			return response.getEntity(QueryResult.class);
		} else {
			//[{"message":"\nLastModifiedById, LastModifiedDate, From Account \n                                   ^\nERROR at Row:1:Column:78\nunexpected token: 'From'","errorCode":"MALFORMED_QUERY"}]
			throw new SalesforceClientException(response.getStatusCode(), response.getEntity(Error.class));
		}
	}
	
	private Identity queryIdentity() {
		HttpResponse response = RestResource.get(token.getId())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.queryParameter("version", "latest")
				.execute();
		
		if (response.getStatusCode() == Status.OK) {
			return response.getEntity(Identity.class);
		} else {
			throw new SalesforceClientException(response.getStatusCode(), response.getEntity(ArrayNode.class));
		}
	}
}