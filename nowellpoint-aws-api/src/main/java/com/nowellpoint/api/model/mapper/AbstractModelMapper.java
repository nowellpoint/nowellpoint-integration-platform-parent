package com.nowellpoint.api.model.mapper;

import static com.mongodb.client.model.Filters.eq;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import com.mongodb.DBRef;
import com.nowellpoint.api.model.document.User;
import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.aws.data.mongodb.MongoDatastore;
import com.nowellpoint.aws.data.mongodb.MongoDocument;
import com.nowellpoint.aws.data.mongodb.MongoDocumentService;
import com.nowellpoint.aws.model.admin.Properties;

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
		
		modelMapper.addConverter(new AbstractConverter<User,AccountProfile>() {

			@Override
			protected AccountProfile convert(User source) {
				AccountProfile accountProfile = new AccountProfile();
				if (source != null && source.getIdentity() != null) {
					
					com.nowellpoint.api.model.document.AccountProfile document = MongoDatastore.getDatabase()
							.getCollection( source.getIdentity().getCollectionName() )
							.withDocumentClass( com.nowellpoint.api.model.document.AccountProfile.class )
							.find( eq ( "_id", new ObjectId( source.getIdentity().getId().toString() ) ) )
							.first();
					
					accountProfile = modelMapper.map(document, AccountProfile.class );
				}
				
				return accountProfile; 
			}			
		});
		
		modelMapper.addConverter(new AbstractConverter<AccountProfile,User>() {

			@Override
			protected User convert(AccountProfile source) {
				String collectionName = MongoDatastore.getCollectionName( com.nowellpoint.api.model.document.AccountProfile.class );
				ObjectId id = null;
				
				User user = new User();
				if (source != null) {					
					user.setHref(source.getHref());
					if (source.getId() == null) {
						
						com.nowellpoint.api.model.document.AccountProfile document = MongoDatastore.getDatabase()
								.getCollection( collectionName )
								.withDocumentClass( com.nowellpoint.api.model.document.AccountProfile.class )
								.find( eq ( "href", source.getHref() ) )
								.first();
						
						id = document.getId();
						
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
		if (Optional.ofNullable(UserContext.getSecurityContext()).isPresent()) {
			return UserContext.getPrincipal().getName();
		} else {
			return System.getProperty(Properties.DEFAULT_SUBJECT);
		}
	}
}
