package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;

import javax.ws.rs.core.UriBuilder;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nowellpoint.api.model.dynamodb.VaultEntry;
import com.nowellpoint.api.rest.domain.ConnectionString;
import com.nowellpoint.api.rest.domain.SalesforceConnectionString;
import com.nowellpoint.api.rest.domain.SalesforceConnector;
import com.nowellpoint.api.rest.domain.SalesforceConnectorList;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.SalesforceConnectorService;
import com.nowellpoint.api.service.SalesforceService;
import com.nowellpoint.api.service.VaultEntryService;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties;
import com.sforce.ws.ConnectionException;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.CountRequest;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.DescribeSobjectRequest;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.GetOrganizationRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.ThemeRequest;
import com.nowellpoint.client.sforce.model.Count;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Photos;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.client.sforce.model.sobject.Sobject;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;

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
	private VaultEntryService vaultEntryService;
	
	private static final AmazonS3 s3Client = new AmazonS3Client();
	
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
		
		SalesforceConnector salesforceConnector = SalesforceConnector.createSalesforceConnector( 
				UserContext.getPrincipal().getName(),
				UserContext.getPrincipal().getName(), 
				identity, 
				organization, 
				vaultEntry, 
				Boolean.TRUE, 
				token.getInstanceUrl(),
				null);
		
		create(salesforceConnector);
		
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
	public void deleteSalesforceConnector(String id) {
		SalesforceConnector salesforceConnector = findById( id );
				
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
	public SalesforceConnector test(String id) {	
		
		SalesforceConnector salesforceConnector = findById( id );
		
		try {
			connect(salesforceConnector.getConnectionString());
			salesforceConnector.setIsValid(Boolean.TRUE);
			salesforceConnector.setStatus("Connection Success");
		} catch (OauthException e) {
			salesforceConnector.setIsValid(Boolean.FALSE);
			salesforceConnector.setStatus(e.getErrorDescription());
		} catch (ValidationException e) {
			salesforceConnector.setIsValid(Boolean.FALSE);
			salesforceConnector.setStatus(e.getMessage());
		} catch (Exception e) {
			salesforceConnector.setIsValid(Boolean.FALSE);
			salesforceConnector.setStatus(e.getMessage());
		}			
		
		update(salesforceConnector);
		
		return salesforceConnector;
	}
	
	@Override
	public SalesforceConnector build(String id) {
		SalesforceConnector salesforceConnector = findById( id );
		
		//build(salesforceConnector);
		
		update(salesforceConnector);
		
		return new SalesforceConnector();
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
	
	private Token connect(String connectionString) throws OauthException, ConnectionException {
		
		VaultEntry vaultEntry = vaultEntryService.retrive(connectionString);
		
		SalesforceConnectionString salesforceConnectionString = SalesforceConnectionString.of(vaultEntry.getValue());
		
		Token token = salesforceService.login(salesforceConnectionString);
		
		return token;
		
	}
	
	private void build(Token token) {
	
		Client client = new Client();
		
		GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
				.setAccessToken(token.getAccessToken())
				.setId(token.getId());
		
		Identity identity = client.getIdentity(getIdentityRequest);
		
		DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
				.setAccessToken(token.getAccessToken())
				.setSobjectsUrl(identity.getUrls().getSobjects());
		
		DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
		
		//salesforceConnector.setSobjects(describeGlobalSobjectsResult.getSobjects().stream().collect(Collectors.toSet()));
		
		ThemeRequest themeRequest = new ThemeRequest()
				.withAccessToken(token.getAccessToken())
				.withRestEndpoint(identity.getUrls().getRest());
		
		Theme theme = client.getTheme(themeRequest);
		
		//salesforceConnector.setTheme(theme);
		
	}
	
	private void describeSobjects(String accessToken, String sobjectsUrl, String queryUrl, DescribeGlobalSobjectsResult describeGlobalSobjectsResult, String instanceKey) throws InterruptedException, ExecutionException, JsonProcessingException {
		ExecutorService executor = Executors.newFixedThreadPool(describeGlobalSobjectsResult.getSobjects().size());
		
		final Client client = new Client();
		
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		
		for (Sobject sobject : describeGlobalSobjectsResult.getSobjects()) {
			executor.submit(() -> {
				DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
						.withAccessToken(accessToken)
						.withSobjectsUrl(sobjectsUrl)
						.withSobject(sobject.getName());

				DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);
				
				CountRequest countRequest = new CountRequest()
						.withAccessToken(accessToken)
						.withQueryUrl(queryUrl)
						.withSobject(sobject.getName());
				
				Count count = client.getCount(countRequest);

				Date now = Date.from(Instant.now());
				
				ObjectId id = new ObjectId( UserContext.getPrincipal().getName() );

				com.nowellpoint.api.model.document.SObjectDetail sobjectDetail = null;
				try {
					sobjectDetail = documentManager.findOne(com.nowellpoint.api.model.document.SObjectDetail.class, and ( eq ( "name", sobject.getName() ), eq ( "instanceKey", instanceKey ) ) );
				} catch (DocumentNotFoundException e) {
					sobjectDetail = new com.nowellpoint.api.model.document.SObjectDetail();
					sobjectDetail.setInstanceKey(instanceKey);
					sobjectDetail.setName(describeSobjectResult.getName());
					sobjectDetail.setCreatedOn(now);
					sobjectDetail.setCreatedBy(documentManager.getReference(com.nowellpoint.api.model.document.UserRef.class, id));
				}
				sobjectDetail.setTotalSize(count.getRecords().get(0).getExpr0());
				sobjectDetail.setLastUpdatedBy(documentManager.getReference(com.nowellpoint.api.model.document.UserRef.class, id));
				sobjectDetail.setLastUpdatedOn(now);
				sobjectDetail.setResult(describeSobjectResult);
				if (Assert.isNull(sobjectDetail.getId())) {
					documentManager.insertOne(sobjectDetail);
				} else {
					documentManager.replaceOne(sobjectDetail);
				}
			});
		}
		
		executor.shutdown();
		executor.awaitTermination(30, TimeUnit.SECONDS);
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