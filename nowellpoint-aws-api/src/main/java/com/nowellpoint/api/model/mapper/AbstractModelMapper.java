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
import com.nowellpoint.api.model.document.User;
import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.model.dto.Id;
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
		modelMapper.addConverter(new AbstractConverter<Id, ObjectId>() {
			
			@Override
			protected ObjectId convert(Id source) {
				return source == null ? null : new ObjectId(source.toString());
			}
		});
		
		modelMapper.addConverter(new AbstractConverter<ObjectId, Id>() {		
			
			@Override
			protected Id convert(ObjectId source) {
				return source == null ? null : new Id(source.toString());
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
				User user = new User();
				if (source != null) {		
					String collectionName = MongoDatastore.getCollectionName( com.nowellpoint.api.model.document.AccountProfile.class );
					ObjectId id = new ObjectId( source.getId().toString() );

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
