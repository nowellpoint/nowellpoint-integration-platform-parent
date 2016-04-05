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
import com.nowellpoint.aws.api.dto.AccountProfileDTO;
import com.nowellpoint.aws.api.event.LoggedInEvent;
import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.AccountProfile;
import com.nowellpoint.aws.data.mongodb.Photos;

public class AccountProfileService extends AbstractDocumentService<AccountProfileDTO, AccountProfile> {
	
	private static final Logger LOGGER = Logger.getLogger(AccountProfileService.class);
	
	public AccountProfileService() {
		super(AccountProfileDTO.class, AccountProfile.class);
	}
	
	/**
	 * 
	 * @param token
	 */
	
	public void loggedInEvent(@Observes LoggedInEvent event) {
		AccountProfileDTO resource = findAccountProfileBySubject(event.getSubject());
		resource.setLastLoginDate(Date.from(Instant.now()));
		
		updateAccountProfile( event.getSubject(), resource, event.getEventSource() );
		
		LOGGER.info("Logged In: " + resource.getHref());
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return the created Identity resource
	 */
	
	public AccountProfileDTO createAccountProfile(AccountProfileDTO resource) {
		resource.setHref(resource.getSubject());
		resource.setUsername(resource.getEmail());
		resource.setName(resource.getFirstName() != null ? resource.getFirstName().concat(" ").concat(resource.getLastName()) : resource.getLastName());
		
		Photos photos = new Photos();
		photos.setProfilePicture("/images/person-generic.jpg");
		
		resource.setPhotos(photos);
		
		create(resource);
		
		hset( resource.getId(), resource.getSubject(), resource );
		hset( resource.getSubject(), AccountProfileDTO.class.getName(), resource );
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return the updated Identity resource
	 */
	
	public AccountProfileDTO updateAccountProfile(String subject, AccountProfileDTO resource, URI eventSource) {
		AccountProfileDTO original = findAccountProfile( resource.getId(), subject );
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
		
		if (resource.getPhotos() == null) {
			resource.setPhotos(original.getPhotos());
		}
		
		replace(subject, resource, eventSource);

		hset( resource.getId(), subject, resource );
		hset( subject, AccountProfileDTO.class.getName(), resource );
		
		return resource;
	}
	
	/**
	 * 
	 * @param id
	 * @return Identity resource for id
	 */
	
	public AccountProfileDTO findAccountProfile(String id, String subject) {
		AccountProfileDTO resource = hget( AccountProfileDTO.class, id, subject );
		
		if ( resource == null ) {
			resource = find(id);
			hset( resource.getId(), subject, resource );
			hset( subject, AccountProfileDTO.class.getName(), resource );
		}
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @return Identity resource for subject
	 */
	
	public AccountProfileDTO findAccountProfileBySubject(String subject) {
		AccountProfileDTO resource = hget( AccountProfileDTO.class, subject, AccountProfileDTO.class.getName() );

		if ( resource == null ) {		

			AccountProfile accountProfile = MongoDBDatastore.getDatabase()
					.getCollection( AccountProfile.class.getAnnotation(Document.class).collectionName() )
					.withDocumentClass( AccountProfile.class )
					.find( eq ( "href", subject ) )
					.first();
			
			if ( accountProfile == null ) {
				throw new WebApplicationException( String.format( "Account Profile for subject: %s does not exist or you do not have access to view", subject ), Status.NOT_FOUND );
			}

			resource = modelMapper.map( accountProfile, AccountProfileDTO.class );

			hset( resource.getId(), subject, resource );
			hset( subject, AccountProfileDTO.class.getName(), resource );
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