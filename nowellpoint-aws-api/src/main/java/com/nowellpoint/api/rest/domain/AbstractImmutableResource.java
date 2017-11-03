package com.nowellpoint.api.rest.domain;

import java.net.URI;
import java.util.Date;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;

import org.bson.types.ObjectId;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties; 

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "id", "createdOn", "lastUpdatedOn" })
public abstract class AbstractImmutableResource implements Resource, Createable, Updateable {

	protected static final ModelMapper modelMapper = new ModelMapper();
	
	static {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		modelMapper.getConfiguration().setMethodAccessLevel(AccessLevel.PUBLIC);
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
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.Subscription, Subscription>() {

			@Override
			protected Subscription convert(com.nowellpoint.api.model.document.Subscription source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableSubscription subscription = modelMapper.map(source, ModifiableSubscription.class);
				return subscription.toImmutable();
			}
			
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.Address, Address>() {

			@Override
			protected Address convert(com.nowellpoint.api.model.document.Address source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableAddress address = modelMapper.map(source, ModifiableAddress.class);
				return address.toImmutable();
			}
			
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.CreditCard, CreditCard>() {

			@Override
			protected CreditCard convert(com.nowellpoint.api.model.document.CreditCard source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableCreditCard creditCard = modelMapper.map(source, ModifiableCreditCard.class);
				return creditCard.toImmutable();
			}
			
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.Contact, Contact>() {

			@Override
			protected Contact convert(com.nowellpoint.api.model.document.Contact source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableContact contact = modelMapper.map(source, ModifiableContact.class);
				return contact.toImmutable();
			}
			
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.UserRef, UserInfo>() {

			@Override
			protected UserInfo convert(com.nowellpoint.api.model.document.UserRef source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableUserInfo userInfo = modelMapper.map(source, ModifiableUserInfo.class);
				return userInfo.toImmutable();
			}
			
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.Photos, Photos>() {

			@Override
			protected Photos convert(com.nowellpoint.api.model.document.Photos source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiablePhotos photos = modelMapper.map(source, ModifiablePhotos.class);
				return photos.toImmutable();
			}
			
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.Transaction, Transaction>() {

			@Override
			protected Transaction convert(com.nowellpoint.api.model.document.Transaction source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableTransaction transaction = modelMapper.map(source, ModifiableTransaction.class);
				return transaction.toImmutable();
			}
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.Organization, OrganizationInfo>() {

			@Override
			protected OrganizationInfo convert(com.nowellpoint.api.model.document.Organization source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableOrganizationInfo organizationInfo = modelMapper.map(source, ModifiableOrganizationInfo.class);
				return organizationInfo.toImmutable();
			}
		});
	}
	
	public abstract @Nullable String getId();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getCreatedOn();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getLastUpdatedOn();
	public abstract void fromDocument(MongoDocument document);
	public abstract MongoDocument toDocument();
	public abstract @Nullable Meta getMeta();
	
	protected <T> Meta getMetaAs(Class<T> resourceClass) {
		URI href = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(resourceClass)
				.path("/{id}")
				.build(Assert.isNotNullOrEmpty(getId()) ? getId() : "{id}");
				
		Meta meta = Meta.builder()
				.href(href.toString())
				.build();
		
		return meta;
	}
}