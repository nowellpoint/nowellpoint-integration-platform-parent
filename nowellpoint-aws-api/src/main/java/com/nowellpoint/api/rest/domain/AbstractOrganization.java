package com.nowellpoint.api.rest.domain;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.OrganizationResource;
import com.nowellpoint.mongodb.document.MongoDocument;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new", depluralize = true, depluralizeDictionary = {"transaction:transactions, user:users"})
@JsonSerialize(as = Organization.class)
@JsonDeserialize(as = Organization.class)
public abstract class AbstractOrganization extends AbstractImmutableResource {
	public abstract UserInfo getCreatedBy();
	public abstract UserInfo getLastUpdatedBy();
	public abstract String getNumber();
	public abstract String getDomain();
	public abstract @Nullable String getName();
	public abstract @Nullable Subscription getSubscription();
	public abstract @Nullable Set<Transaction> getTransactions();
	public abstract @Nullable Set<UserProfile> getUsers();
	
	@Override
	public Meta getMeta() {
		return resourceToMeta(OrganizationResource.class);
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
	
	public static Organization of(com.nowellpoint.api.model.document.Organization source) {
		ModifiableOrganization organization = modelMapper.map(source, ModifiableOrganization.class);
		return organization.toImmutable();
	}
	
	public static Organization updateSubscription(com.nowellpoint.api.model.document.Organization source, JsonNode subscriptionEvent) {
		ModifiableOrganization organization = modelMapper.map(source, ModifiableOrganization.class);
		
		ModifiableSubscription subscription = ModifiableSubscription.create()
				.from(organization.getSubscription())
				.setStatus(subscriptionEvent.get("status").asText())
				.setNextBillingDate(new Date(subscriptionEvent.get("nextBillingDate").asLong()));
		
		Optional<Transaction> optional = organization.getTransactions()
				.stream()
				.filter(transaction -> transaction.getId().equals(subscriptionEvent.get("transaction").get("id").asText()))
				.findAny();
		
		if (! optional.isPresent()) {
			
			ModifiableTransaction transaction = ModifiableTransaction.create()
					.setId(subscriptionEvent.get("transaction").get("id").asText())
					.setAmount(subscriptionEvent.get("transaction").get("amount").asDouble())
					.setCreatedOn(new Date(subscriptionEvent.get("transaction").get("createdAt").asLong()))
					.setCurrencyIsoCode(subscriptionEvent.get("transaction").get("currencyIsoCode").asText())
					.setStatus(subscriptionEvent.get("transaction").get("status").asText())
					.setUpdatedOn(new Date(subscriptionEvent.get("transaction").get("updatedAt").asLong()));
			
			if (! subscriptionEvent.get("transaction").get("creditCard").isNull()) {
				
				Date now = Date.from(Instant.now());
				
				ModifiableCreditCard creditCard = ModifiableCreditCard.create()
						.setAddedOn(now)
						.setUpdatedOn(now)
						.setLastFour(subscriptionEvent.get("transaction").get("creditCard").get("last4").asText())
						.setCardType(subscriptionEvent.get("transaction").get("creditCard").get("cardType").asText())
						.setCardholderName(subscriptionEvent.get("transaction").get("creditCard").get("cardholderName").asText())
						.setExpirationMonth(subscriptionEvent.get("transaction").get("creditCard").get("expirationMonth").asText())
						.setExpirationYear(subscriptionEvent.get("transaction").get("creditCard").get("expirationYear").asText())
						.setImageUrl(subscriptionEvent.get("transaction").get("creditCard").get("imageUrl").asText())
						.setToken(subscriptionEvent.get("transaction").get("creditCard").get("token").asText());
				
				transaction.setCreditCard(creditCard.toImmutable());
			}
			
			organization.addTransaction(transaction.toImmutable());	
		}
		
		organization.setSubscription(subscription.toImmutable());
		
		return organization.toImmutable();
	}
}