/**
 * 
 * Copyright 2015-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.nowellpoint.api.rest.service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mongodb.client.model.Filters;
import com.nowellpoint.api.rest.domain.ConnectionString;
import com.nowellpoint.api.rest.domain.CreateJobRequest;
import com.nowellpoint.api.rest.domain.JobScheduleOptions;
import com.nowellpoint.api.rest.domain.JobType;
import com.nowellpoint.api.rest.domain.RunOnSchedule;
import com.nowellpoint.api.rest.domain.RunWhenSubmitted;
import com.nowellpoint.api.rest.domain.SalesforceConnectionString;
import com.nowellpoint.api.rest.domain.SalesforceConnector;
import com.nowellpoint.api.rest.domain.SalesforceConnectorList;
import com.nowellpoint.api.rest.domain.Service;
import com.nowellpoint.api.rest.domain.Source;
import com.nowellpoint.api.rest.domain.UpdateSalesforceConnectorRequest;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.JobTypeService;
import com.nowellpoint.api.service.SalesforceConnectorService;
import com.nowellpoint.api.service.VaultEntryService;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.GetOrganizationRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Photos;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.util.Properties;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public class SalesforceConnectorServiceImpl extends AbstractSalesforceConnectorService implements SalesforceConnectorService {
	
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
		
		GetIdentityRequest identityRequest = new GetIdentityRequest()
				.setAccessToken(token.getAccessToken())
				.setId(token.getId());
		
		Identity identity = client.getIdentity( identityRequest );
		
		GetOrganizationRequest organizationRequest = new GetOrganizationRequest()
				.setAccessToken(token.getAccessToken())
				.setOrganizationId(identity.getOrganizationId())
				.setSobjectUrl(identity.getUrls().getSobjects());
		
		Organization organization = client.getOrganization( organizationRequest );
		
		identity.getPhotos().setPicture( putImage( token.getAccessToken(), identity.getPhotos().getPicture() ) );
		identity.getPhotos().setThumbnail( putImage( token.getAccessToken(), identity.getPhotos().getThumbnail() ) );
		
		ConnectionString connectString = ConnectionString.salesforce(
				token.getRefreshToken(), 
				token.getId(), 
				SalesforceConnectionString.REFRESH_TOKEN);
		
		UserInfo createdBy = new UserInfo(UserContext.getPrincipal().getName());
		
		SalesforceConnector salesforceConnector = SalesforceConnector.of( 
				createdBy,
				identity, 
				organization, 
				connectString, 
				token);
		
		create(salesforceConnector);
		
		addSalesforceMetadataBackup(salesforceConnector);
		
		refreshConnectionStrings( token.getId(), connectString );

		return salesforceConnector;
	}
	
	/**
	 * 
	 * @param id
	 * @param salesforceConnector
	 */
	
	@Override
	public SalesforceConnector updateSalesforceConnector(String id, UpdateSalesforceConnectorRequest request) {		
		SalesforceConnector source = findById(id);
		SalesforceConnector instance = SalesforceConnector.of(source, request);
		update(instance);
		return instance;
	}
	
	/**
	 * 
	 * @param id
	 * 
	 */
	
	@Override
	public void deleteSalesforceConnector(SalesforceConnector salesforceConnector) {
		
		salesforceConnector.removeConnection();
		
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
		salesforceConnector.connect();
		update(salesforceConnector);
	}
	
	@Override
	public void build(SalesforceConnector salesforceConnector) {
		salesforceConnector.describe();
		update(salesforceConnector);
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
	
	@Override
	public DescribeSobjectResult describeSobject(SalesforceConnector salesforceConnector, String sobject) {
		DescribeSobjectResult result = salesforceConnector.describeSObject(sobject);
		update(salesforceConnector);		
		return result;
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
	
	private void addSalesforceMetadataBackup(SalesforceConnector salesforceConnector) {
		JobType jobType = jobTypeService.findByCode("SALESFORCE_METADATA_BACKUP");
		
		Source source = Source.of(salesforceConnector);
		
		Calendar startAt = getDefaultStartDate(salesforceConnector.getOrganization().getDefaultLocaleSidKey());
		
		CreateJobRequest jobRequest = CreateJobRequest.builder()
				.scheduleOption(JobScheduleOptions.RUN_ON_SCHEDULE)
				.jobType(jobType)
				.source(source)
				.notificationEmail(salesforceConnector.getIdentity().getEmail())
				.schedule(RunOnSchedule.builder()
						.startAt(startAt.getTime())
						.timeInterval(1)
						.timeZone(startAt.getTimeZone())
						.timeUnit(TimeUnit.DAYS)
						.build())
				.build();
		
		jobRequestEvent.fire(jobRequest);
	}
	
	private void refreshConnectionStrings(String id, ConnectionString connectionString) {
		SalesforceConnectorList salesforceConnectorList = query( Filters.eq ( "identity.id", id ));
		salesforceConnectorList.getItems().forEach(salesforceConnector -> {
			vaultEntryService.replace(salesforceConnector.getConnectionString(), connectionString.get());
		});
	}
	
	private Calendar getDefaultStartDate(String localeSidKey) {
		Calendar calendar = Calendar.getInstance(Locale.forLanguageTag(localeSidKey));
		calendar.set(Calendar.HOUR, 3);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.AM_PM, Calendar.AM);
		
		if (Calendar.getInstance(TimeZone.getDefault()).after(calendar)) {
			calendar.roll(Calendar.DATE, 1);
		}
		
		return calendar;
	}
}