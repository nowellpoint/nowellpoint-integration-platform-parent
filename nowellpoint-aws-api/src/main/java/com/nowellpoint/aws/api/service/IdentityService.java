package com.nowellpoint.aws.api.service;

import static com.mongodb.client.model.Filters.eq;

import java.net.URI;
import java.time.Instant;
import java.util.Date;

import javax.enterprise.event.Observes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.api.dto.IdentityDTO;
import com.nowellpoint.aws.api.event.LoggedInEvent;
import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.data.mongodb.Identity;
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
		
		update( subject, resource, event.getEventSource() );
		
		LOGGER.info("Logged In: " + resource.getHref());
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return the created Identity resource
	 */
	
	public IdentityDTO create(String subject, IdentityDTO resource, URI eventSource) {
		resource.setHref(subject);
		resource.setUsername(resource.getEmail());
		resource.setName(resource.getFirstName() != null ? resource.getFirstName().concat(" ").concat(resource.getLastName()) : resource.getLastName());
		
		create(subject, resource, eventSource);
		
		hset( subject, IdentityDTO.class.getName().concat(resource.getId()), resource );
		
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
		
		update(subject, resource, eventSource);

		hset( subject, IdentityDTO.class.getName().concat(resource.getId()), resource );
		
		return resource;
	}
	
	/**
	 * 
	 * @param id
	 * @return Identity resource for id
	 */
	
	public IdentityDTO findIdentity(String id, String subject) {
		IdentityDTO resource = hget( id, subject );
		
		if ( resource == null ) {
			resource = find(subject, id);
			hset( id, subject, resource );
		}
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @return Identity resource for subject
	 */
	
	public IdentityDTO findIdentityBySubject(String subject) {
		//IdentityDTO resource = hget( id, subject );
		
		//
		//
		//
		
		//if ( resource == null ) {
			
			Identity identity = MongoDBDatastore.getDatabase().getCollection( COLLECTION_NAME )
					.withDocumentClass( Identity.class )
					.find( eq ( "href", subject ) )
					.first();
			
			if ( identity == null ) {
				throw new WebApplicationException( String.format( "Identity for subject: %s does not exist or you do not have access to view", subject ), Status.NOT_FOUND );
			}
			
			IdentityDTO resource = modelMapper.map( identity, IdentityDTO.class );

			//hset( subject, IdentityDTO.class.getName().concat(resource.getId()), resource );
		//}
		
		//
		//
		//
		
		return resource;
		
	}
}