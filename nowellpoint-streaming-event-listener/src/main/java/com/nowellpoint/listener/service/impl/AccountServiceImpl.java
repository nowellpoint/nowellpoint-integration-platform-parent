package com.nowellpoint.listener.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.bson.Document;
import org.jboss.logging.Logger;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.nowellpoint.client.sforce.Authenticator;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.model.RefreshTokenRequest;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.listener.model.Account;
import com.nowellpoint.listener.model.AccountEvent;
import com.nowellpoint.listener.model.AccountPayload;
import com.nowellpoint.listener.model.GeoCodedAddress;
import com.nowellpoint.listener.service.AccountService;
import com.nowellpoint.listener.util.CacheManager;
import com.nowellpoint.util.SecretsManager;
import com.nowellpoint.util.SecureValue;

public class AccountServiceImpl implements AccountService {
	
	private static final String ACCOUNTS = "accounts";
	private static final String ADDRESSES = "addresses";
	
	@Inject
	private Logger logger;
	
	@Inject
	private MongoDatabase mongoDatabase;
	
	@Override
	public void processEvent(AccountEvent event) {
		
		if ("CREATE".equals(event.getChangeType())) {
			
			AccountPayload payload = AccountPayload.of(event.getPayload());
			
			GeoCodedAddress billingAddress = GeoCodedAddress.of(payload.getBillingAddress());
			if (billingAddress != null) {
				save(billingAddress);
			}
			
			GeoCodedAddress shippingAddress = GeoCodedAddress.of(payload.getShippingAddress());
			if (shippingAddress != null) {
				save(shippingAddress);
			}
			
			Account account = Account.builder()
					.accountNumber(payload.getAccountNumber())
					.accountSource(payload.getAccountSource())
					.annualRevenue(payload.getAnnualRevenue())
					.billingAddress(payload.getBillingAddress())
					.createdById(payload.getCreatedById())
					.createdDate(payload.getCreatedDate())
					.description(payload.getDescription())
					.events(List.of(event))
					.id(event.getAccountId())
					.industry(payload.getIndustry())
					.lastModifiedById(payload.getLastModifiedById())
					.lastModifiedDate(payload.getLastModifiedDate())
					.name(payload.getName())
					.numberOfEmployees(payload.getNumberOfEmployees())
					.organizationId(event.getOrganizationId())
					.ownerId(payload.getOwnerId())
					.ownership(payload.getOwnership())
					.phone(payload.getPhone())
					.rating(payload.getRating())
					.shippingAddress(payload.getShippingAddress())
					.sic(payload.getSic())
					.sicDesc(payload.getSicDesc())
					.site(payload.getSite())
					.tickerSymbol(payload.getTickerSymbol())
					.type(payload.getType())
					.website(payload.getWebsite())
					.build();
			
			insert(account);
			
		} else if ("UPDATE".equals(event.getChangeType())) {
			
			Account instance = findOrCreate(event.getOrganizationId(), event.getAccountId());
			
			AccountPayload payload = AccountPayload.of(event.getPayload());
			
			GeoCodedAddress billingAddress = GeoCodedAddress.of(payload.getBillingAddress());
			if (billingAddress != null) {
				save(billingAddress);
			}
			
			GeoCodedAddress shippingAddress = GeoCodedAddress.of(payload.getShippingAddress());
			if (shippingAddress != null) {
				save(shippingAddress);
			}
			
			List<AccountEvent> events = new ArrayList<AccountEvent>(instance.getEvents());
			events.add(event);
			
			Map<String,Object> attributes = instance.getAttributesAsMap();
			attributes.putAll(event.getPayload());
			
			payload = AccountPayload.of(attributes);
			
			Account account = instance.toBuilder()
					.accountNumber(payload.getAccountNumber())
					.accountSource(payload.getAccountSource())
					.annualRevenue(payload.getAnnualRevenue())
					.billingAddress(payload.getBillingAddress())
					.createdById(payload.getCreatedById())
					.createdDate(payload.getCreatedDate())
					.description(payload.getDescription())
					.events(events)
					.industry(payload.getIndustry())
					.lastModifiedById(payload.getLastModifiedById())
					.lastModifiedDate(payload.getLastModifiedDate())
					.name(payload.getName())
					.numberOfEmployees(payload.getNumberOfEmployees())
					.organizationId(event.getOrganizationId())
					.ownerId(payload.getOwnerId())
					.ownership(payload.getOwnership())
					.phone(payload.getPhone())
					.rating(payload.getRating())
					.shippingAddress(payload.getShippingAddress())
					.sic(payload.getSic())
					.sicDesc(payload.getSicDesc())
					.site(payload.getSite())
					.tickerSymbol(payload.getTickerSymbol())
					.type(payload.getType())
					.website(payload.getWebsite())
					.build();
			
			replace(account);
			
		} else if ("DELETE".equals(event.getChangeType())) {
			
			Account instance = findOrCreate(event.getOrganizationId(), event.getAccountId());
			
			List<AccountEvent> events = new ArrayList<AccountEvent>(instance.getEvents());
			events.add(event);
			
			Account account = instance.toBuilder()
					.events(events)
					.isDeleted(Boolean.TRUE)
					.build();
			
			replace(account);
			
		} else if ("UNDELETE".equals(event.getChangeType())) {
			
			Account instance = findOrCreate(event.getOrganizationId(), event.getAccountId());
			
			List<AccountEvent> events = new ArrayList<AccountEvent>(instance.getEvents());
			events.add(event);
			
			Account account = instance.toBuilder()
					.events(events)
					.isDeleted(Boolean.FALSE)
					.build();
			
			replace(account);
			
		} else {
			logger.warn(String.format("Unsupported event type: %s", event.getChangeType()));
		}
	}

	private void insert(Account account) {
		mongoDatabase.getCollection(ACCOUNTS, Account.class).insertOne(account);
	}
	
	private void save(GeoCodedAddress address) {
		mongoDatabase.getCollection(ADDRESSES, GeoCodedAddress.class).replaceOne(new Document("_id", address.getId()), address, new ReplaceOptions().upsert(true));
	}
	
	private void replace(Account account) {
		mongoDatabase.getCollection(ACCOUNTS, Account.class).replaceOne(new Document("_id", account.getId()), account);
	}
	
	private Account findAccount(String id) {
		return mongoDatabase.getCollection("accounts", Account.class)
				.find(new Document("_id", id))
				.first();
	}
	
	private Account findOrCreate(String organizationId, String accountId) {
		return Optional.ofNullable(findAccount(accountId))
				.orElseGet(() -> createAccount(CacheManager.get(organizationId), accountId));
	}
	
	private Account createAccount(String refreshToken, String accountId) {
		
		RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
				.clientId(SecretsManager.getSalesforceClientId())
				.clientSecret(SecretsManager.getSalesforceClientSecret())
				.refreshToken(SecureValue.decryptBase64(SecureValue.decryptBase64(refreshToken)))
				.build();
		
		Token token = Authenticator.refreshToken(refreshTokenRequest);
		
		com.nowellpoint.client.sforce.model.Account source = SalesforceClientBuilder.defaultClient(token)
				.findById(com.nowellpoint.client.sforce.model.Account.class, accountId);
		
		Account account = Account.of(source);
		insert(account);
		return account;
	}
}