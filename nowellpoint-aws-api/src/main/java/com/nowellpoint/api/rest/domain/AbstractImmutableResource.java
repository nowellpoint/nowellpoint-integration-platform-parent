package com.nowellpoint.api.rest.domain;

import java.time.Instant;
import java.util.Date;

import javax.annotation.Nullable;

import org.bson.types.ObjectId;
import org.immutables.value.Value;
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
				
				Subscription target = Subscription.of(source);
				return target;
			}
			
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.Plan, Plan>() {

			@Override
			protected Plan convert(com.nowellpoint.api.model.document.Plan source) {
				if (Assert.isNull(source)) {
					return null;
				}
				
				Plan target = Plan.of(source);
				return target;
			}
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.Address, Address>() {

			@Override
			protected Address convert(com.nowellpoint.api.model.document.Address source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableAddress target = modelMapper.map(source, ModifiableAddress.class);
				return target.toImmutable();
			}
			
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.ConnectorType, ConnectorType>() {

			@Override
			protected ConnectorType convert(com.nowellpoint.api.model.document.ConnectorType source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableConnectorType target = modelMapper.map(source, ModifiableConnectorType.class);
				return target.toImmutable();
			}
			
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.CreditCard, CreditCard>() {

			@Override
			protected CreditCard convert(com.nowellpoint.api.model.document.CreditCard source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableCreditCard target = modelMapper.map(source, ModifiableCreditCard.class);
				return target.toImmutable();
			}
			
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.Contact, Contact>() {

			@Override
			protected Contact convert(com.nowellpoint.api.model.document.Contact source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableContact target = modelMapper.map(source, ModifiableContact.class);
				return target.toImmutable();
			}
			
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.Transaction, Transaction>() {

			@Override
			protected Transaction convert(com.nowellpoint.api.model.document.Transaction source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableTransaction target = modelMapper.map(source, ModifiableTransaction.class);
				return target.toImmutable();
			}
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.UserRef, UserInfo>() {

			@Override
			protected UserInfo convert(com.nowellpoint.api.model.document.UserRef source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableUserInfo target = modelMapper.map(source, ModifiableUserInfo.class);
				return target.toImmutable();
			}
			
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.Photos, Photos>() {

			@Override
			protected Photos convert(com.nowellpoint.api.model.document.Photos source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiablePhotos target = modelMapper.map(source, ModifiablePhotos.class);
				return target.toImmutable();
			}
			
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.Organization, OrganizationInfo>() {

			@Override
			protected OrganizationInfo convert(com.nowellpoint.api.model.document.Organization source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableOrganizationInfo target = modelMapper.map(source, ModifiableOrganizationInfo.class);
				return target.toImmutable();
			}
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.Dashboard, Dashboard>() {

			@Override
			protected Dashboard convert(com.nowellpoint.api.model.document.Dashboard source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableDashboard target = modelMapper.map(source, ModifiableDashboard.class);
				return target.toImmutable();
			}
		});
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.SalesforceMetadata, SalesforceMetadata>() {

			@Override
			protected SalesforceMetadata convert(com.nowellpoint.api.model.document.SalesforceMetadata source) {
				if (Assert.isNull(source)) {
					return null;
				}
				ModifiableSalesforceMetadata target = modelMapper.map(source, ModifiableSalesforceMetadata.class);
				return target.toImmutable();
			}
		});
	}
	
	private Instant now = Instant.now();
	
	public abstract @Nullable String getId();
	public abstract void fromDocument(MongoDocument document);
	public abstract MongoDocument toDocument();
	public abstract @Nullable Meta getMeta();
	
	@Value.Default
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	public Date getCreatedOn() {
		return Date.from(now);
	}
	
	@Value.Default
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	public Date getLastUpdatedOn() {
		return Date.from(now);
	}
}