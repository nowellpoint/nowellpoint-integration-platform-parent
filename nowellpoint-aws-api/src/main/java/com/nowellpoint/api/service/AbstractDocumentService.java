package com.nowellpoint.api.service;

import static com.mongodb.client.model.Filters.eq;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import com.mongodb.Block;
import com.mongodb.DBRef;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.nowellpoint.api.dto.AbstractDTO;
import com.nowellpoint.api.dto.AccountProfileDTO;
import com.nowellpoint.api.model.AccountProfile;
import com.nowellpoint.api.model.User;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.MongoDatastore;
import com.nowellpoint.aws.data.mongodb.MongoDocument;

public abstract class AbstractDocumentService<R extends AbstractDTO, D extends MongoDocument> extends AbstractCacheService {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractDocumentService.class);
	
	protected final ModelMapper modelMapper = new ModelMapper();
	
	private final Class<R> resourceType;
	
	private final Class<D> documentType;
	
	/**
	 * 
	 * @param resourceType
	 * @param documentType
	 */
	
	public AbstractDocumentService(Class<R> resourceType, Class<D> documentType) {		
		this.resourceType = resourceType;
		this.documentType = documentType;
		
		configureModelMapper();
	}
	
	/**
	 * 
	 * @param modelMapper
	 */
	
	private void configureModelMapper() {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		modelMapper.getConfiguration().setMethodAccessLevel(AccessLevel.PROTECTED); 
		modelMapper.addConverter(new AbstractConverter<String, ObjectId>() {
			
			@Override
			protected ObjectId convert(String source) {
				return source == null ? null : new ObjectId(source);
			}
		});
		
		modelMapper.addConverter(new AbstractConverter<ObjectId, String>() {		
			
			@Override
			protected String convert(ObjectId source) {
				return source == null ? null : source.toString();
			}
		});
		
		modelMapper.addConverter(new AbstractConverter<User,AccountProfileDTO>() {

			@Override
			protected AccountProfileDTO convert(User source) {
				AccountProfileDTO resource = new AccountProfileDTO();
				if (source != null && source.getIdentity() != null) {
					
					AccountProfile identity = MongoDatastore.getDatabase()
							.getCollection( source.getIdentity().getCollectionName() )
							.withDocumentClass( AccountProfile.class )
							.find( eq ( "_id", new ObjectId( source.getIdentity().getId().toString() ) ) )
							.first();
					
					resource = modelMapper.map(identity, AccountProfileDTO.class );
				}
				
				return resource; 
			}			
		});
		
		modelMapper.addConverter(new AbstractConverter<AccountProfileDTO,User>() {

			@Override
			protected User convert(AccountProfileDTO source) {
				String collectionName = MongoDatastore.getCollectionName( AccountProfile.class );
				ObjectId id = null;
				
				User user = new User();
				if (source != null) {					
					user.setHref(source.getHref());
					if (source.getId() == null) {
						
						AccountProfile identity = MongoDatastore.getDatabase()
								.getCollection( collectionName )
								.withDocumentClass( AccountProfile.class )
								.find( eq ( "href", source.getHref() ) )
								.first();
						
						id = identity.getId();
						
					} else {
						id = new ObjectId( source.getId() );
					}

					DBRef reference = new DBRef( collectionName, id );
					user.setIdentity(reference);

				}
				
				return user; 
			}			
		});
	}
	
	/**
	 * 
	 * @return
	 */
	
	protected String getSubject() {
		return UserContext.getPrincipal().getName();
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	protected R find(String id) {		
		String collectionName = documentType.getAnnotation(Document.class).collectionName();
		
		D document = null;
		try {
			document = MongoDatastore.getDatabase().getCollection( collectionName )
					.withDocumentClass( documentType )
					.find( eq ( "_id", new ObjectId( id ) ) )
					.first();
		} catch (IllegalArgumentException e) {
			
		}

		if ( document == null ) {
			return null;
		}
		
		R resource = modelMapper.map( document, resourceType );
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @return
	 */
	
	protected Set<R> findAllByOwner(String subject) {		
		String collectionName = documentType.getAnnotation(Document.class).collectionName();
		
		FindIterable<D> documents = MongoDatastore.getDatabase()
				.getCollection( collectionName )
				.withDocumentClass( documentType )
				.find( eq ( "owner.href", subject ) );
			
		Set<R> resources = new HashSet<R>();
		
		documents.forEach(new Block<D>() {
			@Override
			public void apply(final D document) {
		        resources.add(modelMapper.map( document, resourceType ));
		    }
		});
		
		return resources;
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return
	 */
	
	protected R create(R resource) {
		String subject = getSubject();
		
		MongoDocument document = modelMapper.map( resource, documentType );		
		document.setCreatedById(subject);
		document.setLastModifiedById(subject);
		
		try {
			MongoDatastore.insertOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Create Document exception", e.getCause());
			throw new ServiceException(Response.Status.BAD_REQUEST, e.getMessage());
		}
		
		modelMapper.map( document, resource );
		
		return resource;		
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return
	 */
	
	protected R replace(R resource) {
		String subject = getSubject();
		
		MongoDocument document = modelMapper.map( resource, documentType );
		document.setLastModifiedById(subject);
		
		try {
			MongoDatastore.replaceOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Update Document exception", e.getCause());
			throw new ServiceException(Response.Status.BAD_REQUEST, e.getMessage());
		}
				
		modelMapper.map( document, resource );
		
		return resource;
	}
	
//	protected R update(String subject, R resource, URI eventSource) {
//		AbstractDocument document = modelMapper.map( resource, documentType );
//		
//		document.setLastModifiedDate(Date.from(Instant.now()));
//		document.setLastModifiedById(subject);
//		
//		try {
//			MongoDBDatastore.updateOne( document.getClass(), document.getId(), );
//		} catch (MongoException e) {
//			LOGGER.error( "Update Document exception", e.getCause());
//			throw new WebApplicationException(e);
//		}
//				
//		modelMapper.map( document, resource );
//		
//		return resource;
//	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 */
	
	protected void delete(R resource) {		
		MongoDocument document = modelMapper.map( resource, documentType );
		try {
			MongoDatastore.deleteOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Delete Document exception", e.getCause());
			throw new ServiceException(Response.Status.BAD_REQUEST, e.getMessage());
		}
	}
}