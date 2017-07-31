package com.nowellpoint.api.rest.domain;

import java.net.URI;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.SignUpService;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Registration.class)
@JsonDeserialize(as = Registration.class)
public abstract class AbstractRegistration extends AbstractImmutableResource {
	public abstract @Nullable Meta getMeta();
	public abstract @Nullable String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract String getCountryCode();
	public abstract @JsonIgnore String getEmailVerificationToken();
	public abstract @Nullable String getDomain();
	public abstract @Nullable URI getEmailVerificationHref();
	public abstract Long getExpiresAt();
	public abstract Subscription getSubscription();
	public abstract @Nullable String getIdentityHref(); 
	public abstract UserInfo getCreatedBy();
	public abstract UserInfo getLastUpdatedBy();
	
	public String getName() {
		return Assert.isNotNullOrEmpty(getFirstName()) ? getFirstName().concat(" ").concat(getLastName()) : getLastName(); 
	}
	
	public static Registration of(com.nowellpoint.api.model.document.Registration source) {

		URI href = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(SignUpService.class)
				.build(source.getId());
				
		Meta meta = Meta.builder()
				.href(href.toString())
				.build();
		
		Subscription subscription = Subscription.builder()
				.addedOn(source.getSubscription().getAddedOn())
				.billingFrequency(source.getSubscription().getBillingFrequency())
				.currencyIsoCode(source.getSubscription().getCurrencyIsoCode())
				.currencySymbol(source.getSubscription().getCurrencySymbol())
				.nextBillingDate(source.getSubscription().getNextBillingDate())
				.planCode(source.getSubscription().getPlanCode())
				.planId(source.getSubscription().getPlanId())
				.planName(source.getSubscription().getPlanName())
				.status(source.getSubscription().getStatus())
				.subscriptionId(source.getSubscription().getSubscriptionId())
				.unitPrice(source.getSubscription().getUnitPrice())
				.updatedOn(source.getSubscription().getUpdatedOn())
				.build();
				
		Registration registration = Registration.builder()
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
				.identityHref(source.getIdentityHref())
				.createdBy(modelMapper.map(source.getCreatedBy(), UserInfo.class))
				.createdOn(source.getCreatedOn())
				.lastUpdatedBy(modelMapper.map(source.getCreatedBy(), UserInfo.class))
				.lastUpdatedOn(source.getLastUpdatedOn())
				.subscription(subscription)
				.meta(meta)
				.build();
			
		return registration;
	}
	
	@Override
	public void fromDocument(MongoDocument document) {
		modelMapper.map(document, this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(Registration.class);
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Registration.class);
	}
}