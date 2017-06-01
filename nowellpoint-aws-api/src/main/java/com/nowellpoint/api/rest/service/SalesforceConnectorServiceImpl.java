package com.nowellpoint.api.rest.service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nowellpoint.api.model.dynamodb.VaultEntry;
import com.nowellpoint.api.rest.domain.ConnectionString;
import com.nowellpoint.api.rest.domain.CreateJobRequest;
import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobType;
import com.nowellpoint.api.rest.domain.RunOnSchedule;
import com.nowellpoint.api.rest.domain.RunWhenSubmitted;
import com.nowellpoint.api.rest.domain.SalesforceConnectionString;
import com.nowellpoint.api.rest.domain.SalesforceConnector;
import com.nowellpoint.api.rest.domain.SalesforceConnectorList;
import com.nowellpoint.api.rest.domain.Service;
import com.nowellpoint.api.rest.domain.Source;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.JobTypeService;
import com.nowellpoint.api.service.SalesforceConnectorService;
import com.nowellpoint.api.service.SalesforceService;
import com.nowellpoint.api.service.VaultEntryService;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.GetOrganizationRequest;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.ThemeRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Photos;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties;
import com.sforce.ws.ConnectionException;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public class SalesforceConnectorServiceImpl extends AbstractSalesforceConnectorService implements SalesforceConnectorService {
	
	@Inject
	private SalesforceService salesforceService;
	
	@Inject
	private JobTypeService jobTypeService;
	
	@Inject
	private VaultEntryService vaultEntryService;
	
	@Inject
	private Event<CreateJobRequest> jobRequestEvent; 
	
	private static final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
	
	/**
	 * 
	 */
	
	public SalesforceConnectorServiceImpl() {
		
	}
	
	/**
	 * @return
	 */
	
	@Override
	public SalesforceConnectorList findAllByOwner(String ownerId) {
		return super.findAllByOwner(ownerId);
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	
	@Override
	public SalesforceConnector createSalesforceConnector(Token token) {
		
		Client client = new Client();
		
		GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
				.setAccessToken(token.getAccessToken())
				.setId(token.getId());
		
		Identity identity = client.getIdentity( getIdentityRequest );
		identity.getPhotos().setPicture( putImage( token.getAccessToken(), identity.getPhotos().getPicture() ) );
		identity.getPhotos().setThumbnail( putImage( token.getAccessToken(), identity.getPhotos().getThumbnail() ) );
		
		GetOrganizationRequest getOrganizationRequest = new GetOrganizationRequest()
				.setAccessToken(token.getAccessToken())
				.setOrganizationId(identity.getOrganizationId())
				.setSobjectUrl(identity.getUrls().getSobjects());
		
		Organization organization = client.getOrganization(getOrganizationRequest);
		
		ConnectionString connectString = ConnectionString.salesforce(
				token.getRefreshToken(), 
				token.getId(), 
				SalesforceConnectionString.REFRESH_TOKEN);
		
		VaultEntry vaultEntry = vaultEntryService.store(connectString.get());
		
		UserInfo createdBy = new UserInfo(UserContext.getPrincipal().getName());
		
		SalesforceConnector salesforceConnector = SalesforceConnector.createSalesforceConnector( 
				createdBy,
				identity, 
				organization, 
				vaultEntry, 
				token);
		
		create(salesforceConnector);
		
		JobType jobType = jobTypeService.findByCode("SALESFORCE_METADATA_BACKUP");
		
		Source source = Source.of(salesforceConnector);
		
		CreateJobRequest jobRequest = CreateJobRequest.builder()
				.scheduleOption(Job.ScheduleOptions.RUN_ON_SCHEDULE)
				.jobType(jobType)
				.source(source)
				.notificationEmail(salesforceConnector.getIdentity().getEmail())
				.schedule(RunOnSchedule.builder()
						.startAt(Date.from(Instant.now()))
						.timeInterval(1)
						.timeZone(TimeZone.getTimeZone("UTC"))
						.timeUnit(TimeUnit.DAYS)
						.build())
				.build();
		
		jobRequestEvent.fire(jobRequest);
		
		return salesforceConnector;
	}
	
	/**
	 * 
	 * @param id
	 * @param salesforceConnector
	 */
	
	@Override
	public SalesforceConnector updateSalesforceConnector(String id, String name, String tag, String ownerId) {		
		SalesforceConnector original = findById(id);
		updateSalesforceConnector(original, name, tag, ownerId, null, null);
		return original;
	}
	
	/**
	 * 
	 * @param id
	 * 
	 */
	
	@Override
	public void deleteSalesforceConnector(SalesforceConnector salesforceConnector) {
				
		String token = SalesforceConnectionString.of(salesforceConnector.getConnectionString()).getCredentials();
		
		vaultEntryService.remove(token);
		
		Photos photos = salesforceConnector.getIdentity().getPhotos();

		List<KeyVersion> keys = new ArrayList<KeyVersion>();
		keys.add(new KeyVersion(photos.getPicture().substring(photos.getPicture().lastIndexOf("/") + 1)));
		keys.add(new KeyVersion(photos.getThumbnail().substring(photos.getThumbnail().lastIndexOf("/") + 1)));

		DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest("nowellpoint-profile-photos").withKeys(keys);

		s3Client.deleteObjects(deleteObjectsRequest);
		
		delete(salesforceConnector);
	}
	
	/**
	 * 
	 *  @param id
	 *  
	 */
	
	@Override
	public SalesforceConnector findById(String id) {		
		return super.findById(id);
	}
	
	/**
	 * 
	 */
	
	@Override
	public void test(SalesforceConnector salesforceConnector) {	
		
		try {
			Token token = connect(salesforceConnector.getConnectionString());
			salesforceConnector.setIsValid(Boolean.TRUE);
			salesforceConnector.setStatus(token.getIssuedAt());
		} catch (OauthException e) {
			salesforceConnector.setIsValid(Boolean.FALSE);
			salesforceConnector.setStatus("Error: " + e.getErrorDescription());
		} catch (ValidationException e) {
			salesforceConnector.setIsValid(Boolean.FALSE);
			salesforceConnector.setStatus("Error: " + e.getMessage());
		} catch (Exception e) {
			salesforceConnector.setIsValid(Boolean.FALSE);
			salesforceConnector.setStatus("Error: " + e.getMessage());
		} finally {
			salesforceConnector.setLastTestedOn(Date.from(Instant.now()));
			update(salesforceConnector);
		}
	}
	
	@Override
	public void build(SalesforceConnector salesforceConnector) {
		
		try {
			Token token = connect(salesforceConnector.getConnectionString());
			
			DescribeGlobalSobjectsResult describeGlobalSobjectsResult = describe(token.getAccessToken(), salesforceConnector.getIdentity().getUrls().getSobjects());
			
			salesforceConnector.setSobjects(describeGlobalSobjectsResult.getSobjects().stream().collect(Collectors.toSet()));
			
			Theme theme = getTheme(token.getAccessToken(), salesforceConnector.getIdentity().getUrls().getRest());
			
			salesforceConnector.setTheme(theme);
			
			salesforceConnector.setIsValid(Boolean.TRUE);
			salesforceConnector.setStatus(token.getIssuedAt());
		} catch (OauthException e) {
			salesforceConnector.setIsValid(Boolean.FALSE);
			salesforceConnector.setStatus("Error: " + e.getErrorDescription());
		} catch (Exception e) {
			salesforceConnector.setIsValid(Boolean.FALSE);
			salesforceConnector.setStatus("Error: " + e.getMessage());
		} finally {
			salesforceConnector.setLastTestedOn(Date.from(Instant.now()));
			update(salesforceConnector);
		}
	}
	
	@Override
	public void metadataBackup(SalesforceConnector salesforceConnector) {
		
		JobType jobType = jobTypeService.findByCode("SALESFORCE_METADATA_BACKUP");
		
		Source source = Source.of(salesforceConnector);
		
		CreateJobRequest jobRequest = CreateJobRequest.builder()
				.schedule(RunWhenSubmitted.builder().build())
				.jobType(jobType)
				.source(source)
				.notificationEmail(salesforceConnector.getIdentity().getEmail())
				.build();
		
		jobRequestEvent.fire(jobRequest);
	}
	
	/**
	 * 
	 * @param accessToken
	 * @param imageUrl
	 * @return
	 */
	
	private String putImage(String accessToken, String imageUrl) {
		
		try {
			URL url = new URL( imageUrl + "?oauth_token=" + accessToken );
			
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			String contentType = connection.getHeaderField("Content-Type");
			
			ObjectMetadata objectMetadata = new ObjectMetadata();
	    	objectMetadata.setContentLength(connection.getContentLength());
	    	objectMetadata.setContentType(contentType);
	    	
	    	String key = UUID.randomUUID().toString().replace("-", "");
			
	    	PutObjectRequest putObjectRequest = new PutObjectRequest("nowellpoint-profile-photos", key, connection.getInputStream(), objectMetadata);
	    	
	    	s3Client.putObject(putObjectRequest);
	    	
	    	URI uri = UriBuilder.fromUri(System.getProperty(Properties.CLOUDFRONT_HOSTNAME))
					.path("{id}")
					.build(key);
			
			return uri.toString();
			
		} catch (IOException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	public SalesforceConnector addService(String id, String jobTypeId) {
		
		SalesforceConnector salesforceConnector = findById( id );
		
		JobType jobType = jobTypeService.findById( jobTypeId );
		
		Service service = Service.of(jobType);
		
		salesforceConnector.addService(service);
		
		update(salesforceConnector);
		
		return salesforceConnector;
		
	}
	
	public Service getService(String id, String serviceId) {
		
		SalesforceConnector salesforceConnector = findById( id );
		
		Service service = salesforceConnector.getService(serviceId);
		
		return service;
	}
	
	private Token connect(String connectionString) throws OauthException, ConnectionException {
		
		VaultEntry vaultEntry = vaultEntryService.retrive(connectionString);
		
		SalesforceConnectionString salesforceConnectionString = SalesforceConnectionString.of(vaultEntry.getValue());
		
		Token token = salesforceService.login(salesforceConnectionString);
		
		return token;
		
	}
	
	private DescribeGlobalSobjectsResult describe(String accessToken, String sobjectsUrl) {
		Client client = new Client();
		
		DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
				.setAccessToken(accessToken)
				.setSobjectsUrl(sobjectsUrl);
		
		DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
		
		return describeGlobalSobjectsResult;
	}
	
	private Theme getTheme(String accessToken, String restUrl) {
		Client client = new Client();
		
		ThemeRequest themeRequest = new ThemeRequest()
				.withAccessToken(accessToken)
				.withRestEndpoint(restUrl);
		
		Theme theme = client.getTheme(themeRequest);
		
		return theme;
		
	}
	
	private void updateSalesforceConnector(SalesforceConnector salesforceConnector, String name, String tag, String ownerId, Boolean isValid, String connectionStatus) {
		if (Assert.isNullOrEmpty(name)) {
			name = salesforceConnector.getName();
		}
		
		if (Assert.isNullOrEmpty(ownerId)) {
			ownerId = salesforceConnector.getOwner().getId();
		}
		
		if (Assert.isNull(tag)) {
			tag = salesforceConnector.getTag();
		} else if (Assert.isEmpty(tag)) {
			tag = null;
		}
		
		if (Assert.isNullOrEmpty(connectionStatus)) {
			connectionStatus = salesforceConnector.getStatus();
		}
		
		if (Assert.isNull(isValid)) {
			isValid = salesforceConnector.getIsValid();
		}
		
		salesforceConnector.setName(name);
		salesforceConnector.setTag(tag);
		salesforceConnector.setOwner(new UserInfo(ownerId));
		salesforceConnector.setIsValid(isValid);
		salesforceConnector.setStatus(connectionStatus);
		salesforceConnector.setLastUpdatedBy(new UserInfo(UserContext.getPrincipal().getName()));
		salesforceConnector.setLastUpdatedOn(Date.from(Instant.now()));
		
		update(salesforceConnector);
	}
}