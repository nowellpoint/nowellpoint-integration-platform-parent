package com.nowellpoint.api.model.domain;

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
import com.nowellpoint.mongodb.Datastore;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
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
					
					System.out.println(source.getIdentity().getCollectionName() );
					System.out.println(source.getIdentity().getId() );
					
					DocumentManager documentManager = Datastore.getCurrentSession().createDocumentManager();
					
//					com.nowellpoint.api.model.document.AccountProfile document = documentManager.findOne( 
//							com.nowellpoint.api.model.document.AccountProfile.class, 
//							new ObjectId( source.getIdentity().getId().toString() ) );
					
					com.nowellpoint.api.model.document.AccountProfile document = new com.nowellpoint.api.model.document.AccountProfile();
					document.setName("My name");
					
					userInfo = modelMapper.map( document, UserInfo.class );
					
					System.out.println( userInfo.getCompany() );
				}
				
				return userInfo; 
			}			
		});
		
		modelMapper.addConverter(new AbstractConverter<UserInfo,UserRef>() {

			@Override
			protected UserRef convert(UserInfo source) {
				UserRef user = new UserRef();
				if (source != null) {		
					String collectionName = Datastore.getCurrentSession().resolveCollectionName(com.nowellpoint.api.model.document.AccountProfile.class);
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
	
	protected DocumentManagerFactory datastore;
		
	public AbstractResource() {
		
	}
	
	public <T> AbstractResource(T document) {
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
	
	public abstract MongoDocument toDocument();
}