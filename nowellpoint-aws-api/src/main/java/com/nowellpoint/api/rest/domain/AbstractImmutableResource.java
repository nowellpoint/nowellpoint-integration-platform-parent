package com.nowellpoint.api.rest.domain;

import java.net.URI;
import java.util.Date;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;

import org.bson.types.ObjectId;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.modelmapper.Provider.ProvisionRequest;
import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.nowellpoint.api.model.document.Subscription;
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
		
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.api.model.document.Subscription, AbstractSubscription>() {

			@Override
			protected AbstractSubscription convert(Subscription source) {
				ModifiableSubscription subscription = modelMapper.map(source, ModifiableSubscription.class);
				return subscription.toImmutable();
			}
			
		});
		
//		modelMapper.createTypeMap(com.nowellpoint.api.model.document.Subscription.class, AbstractSubscription.class).setProvider(
//				new Provider<AbstractSubscription>() {
//					@Override
//					public AbstractSubscription get(ProvisionRequest<AbstractSubscription> request) {
//						System.out.println("made it");
//						com.nowellpoint.api.model.document.Subscription source = com.nowellpoint.api.model.document.Subscription.class.cast(request.getSource());
//						ModifiableSubscription subscription = modelMapper.map(source, ModifiableSubscription.class);
//						return subscription.toImmutable();
//					}
//				});
		

	}
	
	public abstract @Nullable String getId();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getCreatedOn();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getLastUpdatedOn();
	public abstract void fromDocument(MongoDocument document);
	public abstract MongoDocument toDocument();
	
	protected <T> Meta getMetaAs(Class<T> resourceClass) {
		URI href = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(resourceClass)
				.build(Assert.isNotNullOrEmpty(getId()) ? getId() : "{id}");
				
		Meta meta = Meta.builder()
				.href(href.toString())
				.build();
		
		return meta;
	}
}