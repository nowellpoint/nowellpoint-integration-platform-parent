package com.nowellpoint.api.rest.domain;

import java.net.URI;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.OrganizationResource;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Properties;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true, depluralize = true, depluralizeDictionary = {"referenceLink:referenceLinks"})
@JsonSerialize(as = Organization.class)
@JsonDeserialize(as = Organization.class)
public abstract class AbstractOrganization extends AbstractImmutableResource {
	public abstract UserInfo getCreatedBy();
	public abstract UserInfo getLastUpdatedBy();
	public abstract @Nullable String getNumber();
	public abstract String getDomain();
	public abstract @Nullable String getName();
	public abstract @Nullable Subscription getSubscription();
	public abstract @Nullable CreditCard getCreditCard();
	public abstract @Nullable Address getBillingAddress();
	public abstract @Nullable Contact getBillingContact();
	public abstract @Nullable Set<Transaction> getTransactions();
	public abstract @JsonIgnore Set<ReferenceLink> getReferenceLinks();
	
	public static Organization of(com.nowellpoint.api.model.document.Organization source) {
		
		URI href = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(OrganizationResource.class)
				.build(source.getId());
				
		Meta meta = Meta.builder()
				.href(href.toString())
				.build();
		
		Address billingAddress = Address.builder()
				.city(source.getBillingAddress().getCity())
				.country(source.getBillingAddress().getCountry())
				.countryCode(source.getBillingAddress().getCountryCode())
				.id(source.getBillingAddress().getId())
				.latitude(source.getBillingAddress().getLatitude())
				.longitude(source.getBillingAddress().getLongitude())
				.postalCode(source.getBillingAddress().getPostalCode())
				.state(source.getBillingAddress().getState())
				.stateCode(source.getBillingAddress().getStateCode())
				.street(source.getBillingAddress().getStreet())
				.build();
		
		Contact billingContact = Contact.builder()
				.firstName(source.getBillingContact().getFirstName())
				.lastName(source.getBillingContact().getLastName())
				.build();
		
		CreditCard creditCard = CreditCard.builder()
				.addedOn(source.getCreditCard().getAddedOn())
				.cardholderName(source.getCreditCard().getCardholderName())
				.cardType(source.getCreditCard().getCardType())
				.expirationMonth(source.getCreditCard().getExpirationMonth())
				.expirationYear(source.getCreditCard().getExpirationYear())
				.imageUrl(source.getCreditCard().getImageUrl())
				.lastFour(source.getCreditCard().getLastFour())
				.number(source.getCreditCard().getNumber())
				.token(source.getCreditCard().getToken())
				.updatedOn(source.getCreditCard().getUpdatedOn())
				.build();
		
		Organization organization = Organization.builder()
				.id(source.getId() == null ? null : source.getId().toString())
				.billingAddress(billingAddress)
				.billingContact(billingContact)
				.createdBy(modelMapper.map(source.getCreatedBy(), UserInfo.class))
				.createdOn(source.getCreatedOn())
				.lastUpdatedBy(modelMapper.map(source.getCreatedBy(), UserInfo.class))
				.lastUpdatedOn(source.getLastUpdatedOn())
				.creditCard(creditCard)
				.domain(source.getDomain())
				.meta(meta)
				.name(source.getName())
				.number(source.getNumber())
				.build();
		
		return organization;
	}
	
	@Override
	public void fromDocument(MongoDocument document) {
		modelMapper.map(document, this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(Organization.class);
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Organization.class);
	}
}