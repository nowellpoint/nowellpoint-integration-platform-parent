package com.nowellpoint.aws.api.service;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.Date;

import javax.enterprise.event.Observes;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nowellpoint.aws.api.dto.IdentityDTO;
import com.nowellpoint.aws.api.event.LoggedInEvent;
import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.data.mongodb.Identity;
import com.nowellpoint.aws.data.mongodb.Photos;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.tools.TokenParser;

public class IdentityService extends AbstractDataService<IdentityDTO, Identity> {
	
	private static final Logger LOGGER = Logger.getLogger(IdentityService.class);
	
	private static final String COLLECTION_NAME = "identities";
	
	public IdentityService() {
		super(IdentityDTO.class, Identity.class);
	}
	
	/**
	 * 
	 * @param token
	 */
	
	public void loggedInEvent(@Observes LoggedInEvent event) {
		String subject = TokenParser.getSubject(System.getProperty(Properties.STORMPATH_API_KEY_SECRET), event.getToken().getAccessToken());
		
		IdentityDTO resource = findIdentityBySubject(subject);
		resource.setLastLoginDate(Date.from(Instant.now()));
		
		updateIdentity( subject, resource, event.getEventSource() );
		
		LOGGER.info("Logged In: " + resource.getHref());
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return the created Identity resource
	 */
	
	public IdentityDTO createIdentity(String subject, IdentityDTO resource, URI eventSource) {
		resource.setHref(subject);
		resource.setUsername(resource.getEmail());
		resource.setName(resource.getFirstName() != null ? resource.getFirstName().concat(" ").concat(resource.getLastName()) : resource.getLastName());
		
		Photos photos = new Photos();
		photos.setProfilePicture("/images/person-generic.jpg");
		
		resource.setPhotos(photos);
		
		create(subject, resource, eventSource);
		
		hset( resource.getId(), subject, resource );
		hset( subject, IdentityDTO.class.getName(), resource );
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return the updated Identity resource
	 */
	
	public IdentityDTO updateIdentity(String subject, IdentityDTO resource, URI eventSource) {
		IdentityDTO original = findIdentity( resource.getId(), subject );
		resource.setName(resource.getFirstName() != null ? resource.getFirstName().concat(" ").concat(resource.getLastName()) : resource.getLastName());
		resource.setCreatedById(original.getCreatedById());
		resource.setCreatedDate(original.getCreatedDate());
		resource.setHref(original.getHref());
		resource.setEmailEncodingKey(original.getEmailEncodingKey());
		resource.setIsActive(original.getIsActive());
		resource.setLocaleSidKey(original.getLocaleSidKey());
		resource.setTimeZoneSidKey(original.getTimeZoneSidKey());
		
		if (resource.getLastLoginDate() == null) {
			resource.setLastLoginDate(original.getLastLoginDate());
		}
		
		update(subject, resource, eventSource);

		hset( resource.getId(), subject, resource );
		hset( subject, IdentityDTO.class.getName(), resource );
		
		return resource;
	}
	
	/**
	 * 
	 * @param id
	 * @return Identity resource for id
	 */
	
	public IdentityDTO findIdentity(String id, String subject) {
		IdentityDTO resource = hget( IdentityDTO.class, id, subject );
		
		if ( resource == null ) {
			resource = find(id);
			hset( resource.getId(), subject, resource );
			hset( subject, IdentityDTO.class.getName(), resource );
		}
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @return Identity resource for subject
	 */
	
	public IdentityDTO findIdentityBySubject(String subject) {
		IdentityDTO resource = hget( IdentityDTO.class, subject, IdentityDTO.class.getName() );

		if ( resource == null ) {			
			Identity identity = MongoDBDatastore.getDatabase()
					.getCollection( COLLECTION_NAME )
					.withDocumentClass( Identity.class )
					.find( eq ( "href", subject ) )
					.first();
			
			if ( identity == null ) {
				throw new WebApplicationException( String.format( "Identity for subject: %s does not exist or you do not have access to view", subject ), Status.NOT_FOUND );
			}
			
			resource = modelMapper.map( identity, IdentityDTO.class );

			hset( resource.getId(), subject, resource );
			hset( subject, IdentityDTO.class.getName(), resource );
		}
		
		return resource;		
	}
	
	public void addSalesforceProfilePicture(String userId, String profileHref) {
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		try {
			URL url = new URL( profileHref );
			
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			String contentType = connection.getHeaderField("Content-Type");
			
			ObjectMetadata objectMetadata = new ObjectMetadata();
	    	objectMetadata.setContentLength(connection.getContentLength());
	    	objectMetadata.setContentType(contentType);
			
	    	PutObjectRequest putObjectRequest = new PutObjectRequest("aws-microservices", userId, connection.getInputStream(), objectMetadata);
	    	
	    	s3Client.putObject(putObjectRequest);
			
		} catch (IOException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}