package com.nowellpoint.api.rest.domain;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.OrganizationResource;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

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
	public abstract @Nullable Set<UserInfo> getUsers();
	
	@Override
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(OrganizationResource.class)
				.build();
	}
	
	@Override
	public void replace(MongoDocument document) {
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
		ModifiableOrganization organization = sourceToModifiableOrganization(source);
		return organization.toImmutable();
	}
	
	public static Organization of(com.nowellpoint.api.model.document.Organization source, JsonNode subscriptionEvent) {
		
		ModifiableOrganization organization = sourceToModifiableOrganization(source);
				
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
	
	private static ModifiableOrganization sourceToModifiableOrganization(com.nowellpoint.api.model.document.Organization source) {
		if (Assert.isNull(source)) {
			return null;
		}
		
		Set<Transaction> transactions = source.getTransactions().stream()
				.map(t -> Transaction.of(t))
				.collect(Collectors.toSet());
		
		ModifiableOrganization organization = new ModifiableOrganization()
				.setCreatedBy(UserInfo.of(source.getCreatedBy()))
				.setCreatedOn(source.getCreatedOn())
				.setDomain(source.getDomain())
				.setId(source.getId().toString())
				.setLastUpdatedBy(UserInfo.of(source.getLastUpdatedBy()))
				.setLastUpdatedOn(source.getLastUpdatedOn())
				.setName(source.getName())
				.setNumber(source.getNumber())
				.setSubscription(Subscription.of(source.getSubscription()))
				.setTransactions(transactions);
		
		return organization;
	}
}