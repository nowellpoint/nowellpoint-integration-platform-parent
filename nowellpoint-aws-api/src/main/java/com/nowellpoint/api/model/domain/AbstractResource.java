package com.nowellpoint.api.model.domain;

import static com.mongodb.client.model.Filters.eq;

import java.util.Date;

import org.bson.types.ObjectId;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBRef;
import com.nowellpoint.api.model.document.UserRef;
import com.nowellpoint.mongodb.document.MongoDatastore;
import com.nowellpoint.mongodb.document.MongoDocument;

@JsonInclude(Include.NON_EMPTY)
public abstract class AbstractResource implements Resource, Createable, Updateable {
	
	protected static final ModelMapper modelMapper = new ModelMapper();
	
	static {
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
	
	private String id;
	
	private Date createdDate;
	
	private Date lastModifiedDate;
	
	private Date systemCreatedDate;
	
	private Date systemModifiedDate;
	
	private Meta meta;
		
	public AbstractResource() {
		
	}
	
	public <T extends MongoDocument> AbstractResource(T document) {
		modelMapper.map(document, this);
	}
 	
	public AbstractResource(String id) {
		setId(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	public Date getSystemCreatedDate() {
		return systemCreatedDate;
	}

	public void setSystemCreatedDate(Date systemCreatedDate) {
		this.systemCreatedDate = systemCreatedDate;
	}

	public Date getSystemModifiedDate() {
		return systemModifiedDate;
	}

	public void setSystemModifiedDate(Date systemModifiedDate) {
		this.systemModifiedDate = systemModifiedDate;
	}
	
	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
	
	@JsonIgnore
	public String toJson() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}
}