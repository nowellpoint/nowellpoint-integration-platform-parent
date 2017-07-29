package com.nowellpoint.api.rest.domain;

import java.util.Date;

import javax.annotation.Nullable;

import org.bson.types.ObjectId;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.mongodb.document.MongoDocument; 

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "id", "createdOn", "lastUpdatedOn" })
public abstract class AbstractImmutableResource implements Resource, Createable, Updateable {

	protected static final ModelMapper modelMapper = new ModelMapper();
	
	static {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		modelMapper.getConfiguration().setMethodAccessLevel(AccessLevel.PRIVATE);
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
		modelMapper.getConfiguration().setFieldAccessLevel(AccessLevel.PRIVATE);
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
		
		modelMapper.createTypeMap(com.nowellpoint.api.model.document.Registration.class, Registration.class).setProvider(
				new Provider<Registration>() {
					public Registration get(ProvisionRequest<Registration> request) {
						com.nowellpoint.api.model.document.Registration source = com.nowellpoint.api.model.document.Registration.class.cast(request.getSource());
						
//						URI href = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
//								.path(RegistrationService.class)
//								.build(source.getId());
//						
//						Meta meta = Meta.builder()
//								.href(href.toString())
//								.build();
						
						Registration destination = Registration.builder()
								.id(source.getId() == null ? null : source.getId().toString())
								.countryCode(source.getCountryCode())
								.createdOn(source.getCreatedOn())
								.email(source.getEmail())
								.emailVerificationToken(source.getEmailVerificationToken())
								.domain(source.getDomain())
								.firstName(source.getFirstName())
								.lastName(source.getLastName())
								.lastUpdatedOn(source.getLastUpdatedOn())
								.expiresAt(source.getExpiresAt())
								.createdBy(modelMapper.map(source.getCreatedBy(), UserInfo.class))
								.lastUpdatedBy(modelMapper.map(source.getCreatedBy(), UserInfo.class))
								.identityHref(source.getIdentityHref())
								//.meta(meta)
								.build();
						
						return destination;
					}
		        });
	}
	
	public abstract @Nullable String getId();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getCreatedOn();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getLastUpdatedOn();
	
	@JsonIgnore
	public String toJson() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}
	
	@JsonIgnore
	public void fromDocument(MongoDocument document) {
		modelMapper.map(document, this);
	}
	
	public abstract MongoDocument toDocument();
}