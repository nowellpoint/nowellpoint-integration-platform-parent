package com.nowellpoint.api.model.mapper;

import static com.mongodb.client.model.Filters.eq;

import java.util.Optional;
import java.util.Set;

import org.bson.types.ObjectId;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import com.mongodb.DBRef;
import com.nowellpoint.api.model.document.UserRef;
import com.nowellpoint.api.model.domain.UserInfo;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.mongodb.document.MongoDatastore;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.mongodb.document.MongoDocumentService;

public class AbstractModelMapper<T extends MongoDocument> extends MongoDocumentService<T> {
	
	protected final ModelMapper modelMapper = new ModelMapper();
	
	public AbstractModelMapper(Class<T> documentType) {		
		super(documentType);
		
		configureModelMapper();
	}

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
		
		modelMapper.addConverter(new AbstractConverter<UserRef,UserInfo>() {

			@Override
			protected UserInfo convert(UserRef source) {
				UserInfo userInfo = new UserInfo();
				if (source != null && source.getIdentity() != null) {
					
					com.nowellpoint.api.model.document.AccountProfile document = MongoDatastore.getDatabase()
							.getCollection( source.getIdentity().getCollectionName() )
							.withDocumentClass( com.nowellpoint.api.model.document.AccountProfile.class )
							.find( eq ( "_id", new ObjectId( source.getIdentity().getId().toString() ) ) )
							.first();
					
					userInfo = modelMapper.map(document, UserInfo.class );
				}
				
				return userInfo; 
			}			
		});
		
		modelMapper.addConverter(new AbstractConverter<UserInfo,UserRef>() {

			@Override
			protected UserRef convert(UserInfo source) {
				UserRef user = new UserRef();
				if (source != null) {		
					String collectionName = MongoDatastore.getCollectionName( com.nowellpoint.api.model.document.AccountProfile.class );
					ObjectId id = new ObjectId( source.getId() );

					DBRef reference = new DBRef( collectionName, id );
					user.setIdentity(reference);
				}
				
				return user; 
			}			
		});
	}
	
	/**
	 * 
	 * 
	 * @param owner
	 * @return Collection of documents for owner
	 * 
	 * 
	 */
	
	protected Set<T> findAllByOwner(String owner) {		
		String collectionName = MongoDatastore.getCollectionName( com.nowellpoint.api.model.document.AccountProfile.class );
		ObjectId id = new ObjectId( owner );
		Set<T> documents = find( eq ( "owner.identity", new DBRef( collectionName, id )));
		return documents;
	}
	
	/**
	 * 
	 * @return
	 */
	
	protected String getSubject() {
		if (Optional.ofNullable(UserContext.getSecurityContext()).isPresent()) {
			return UserContext.getPrincipal().getName();
		} else {
			return System.getProperty(Properties.DEFAULT_SUBJECT);
		}
	}
}
