package com.nowellpoint.listener.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;

import org.bson.Document;
import org.jboss.logging.Logger;

import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.changeevent.ChangeEvent;
import com.nowellpoint.listener.connection.MongoConnection;
import com.nowellpoint.listener.model.Account;
import com.nowellpoint.listener.model.AccountEvent;
import com.nowellpoint.listener.model.AccountPayload;
import com.nowellpoint.listener.model.TopicConfiguration;
import com.nowellpoint.util.SecretsManager;
import com.nowellpoint.util.SecureValue;
import com.nowellpoint.util.SecureValueException;

public class AccountService {
	
	private static final Logger LOGGER = Logger.getLogger(AccountService.class);
	private static final String ACCOUNTS = "accounts";
	
	public void processChangeEvent(ChangeEvent event, TopicConfiguration configuration) throws SecureValueException {
		Jsonb jsonb = JsonbBuilder.create(new JsonbConfig()
				.withNullValues(Boolean.TRUE)
				.withPropertyVisibilityStrategy(
						new PropertyVisibilityStrategy() {
							
							@Override
							public boolean isVisible(Field field) {
								return true;
							}
							
							@Override
							public boolean isVisible(Method method) {
								return false;
							}
							
						}));
		
		String accountId = event.getPayload().getChangeEventHeader().getRecordIds().get(0);
		
		if ("CREATE".equals(event.getPayload().getChangeEventHeader().getChangeType())) {
			
			String json = jsonb.toJson(event.getPayload().getAttributes());
			
			AccountPayload payload = jsonb.fromJson(json, AccountPayload.class);
			
			AccountEvent accountEvent = AccountEvent.builder()
					.payload(payload)
					.userId(event.getPayload().getChangeEventHeader().getCommitUser())
					.timestamp(event.getPayload().getChangeEventHeader().getCommitTimestamp())
					.transactionKey(event.getPayload().getChangeEventHeader().getTransactionKey())
					.type(event.getPayload().getChangeEventHeader().getChangeType())
					.build();
			
			Account account = Account.builder()
					.accountNumber(payload.getAccountNumber())
					.accountSource(payload.getAccountSource())
					.annualRevenue(payload.getAnnualRevenue())
					.billingAddress(payload.getBillingAddress())
					.createdById(payload.getCreatedById())
					.createdDate(payload.getCreatedDate())
					.description(payload.getDescription())
					.events(List.of(accountEvent))
					.id(accountId)
					.industry(payload.getIndustry())
					.lastModifiedById(payload.getLastModifiedById())
					.lastModifiedDate(payload.getLastModifiedDate())
					.name(payload.getName())
					.numberOfEmployees(payload.getNumberOfEmployees())
					.organizationId(configuration.getOrganizationId())
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
			
		} else if ("UPDATE".equals(event.getPayload().getChangeEventHeader().getChangeType())) {
			
			Account instance = findAccount(accountId);
			
			if (instance == null) {
				instance = buildAccount(configuration, accountId);
				insert(instance);
			}
			
			if (event.getPayload().getAttributes() != null)  {
				
				String json = jsonb.toJson(event.getPayload().getAttributes());
				
				AccountPayload payload = jsonb.fromJson(json, AccountPayload.class);
				
				AccountEvent accountEvent = AccountEvent.builder()
						.payload(payload)
						.userId(event.getPayload().getChangeEventHeader().getCommitUser())
						.timestamp(event.getPayload().getChangeEventHeader().getCommitTimestamp())
						.transactionKey(event.getPayload().getChangeEventHeader().getTransactionKey())
						.type(event.getPayload().getChangeEventHeader().getChangeType())
						.build();
				
				List<AccountEvent> events = new ArrayList<AccountEvent>(instance.getEvents());
				events.add(accountEvent);
				
				Map<String,Object> attributes = instance.getAttributesAsMap();
				attributes.putAll(event.getPayload().getAttributes());
				
				json = jsonb.toJson(attributes);
				
				payload = jsonb.fromJson(json, AccountPayload.class);
				
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
			}
			
		} else if ("DELETE".equals(event.getPayload().getChangeEventHeader().getChangeType())) {
			
			Account instance = findAccount(accountId);
			
			AccountEvent accountEvent = AccountEvent.builder()
					.userId(event.getPayload().getChangeEventHeader().getCommitUser())
					.timestamp(event.getPayload().getChangeEventHeader().getCommitTimestamp())
					.transactionKey(event.getPayload().getChangeEventHeader().getTransactionKey())
					.type(event.getPayload().getChangeEventHeader().getChangeType())
					.build();
			
			List<AccountEvent> events = new ArrayList<AccountEvent>(instance.getEvents());
			events.add(accountEvent);
			
			Account account = instance.toBuilder()
					.events(events)
					.isDeleted(Boolean.TRUE)
					.build();
			
			replace(account);
			
		} else if ("UNDELETE".equals(event.getPayload().getChangeEventHeader().getChangeType())) {
			
			Account instance = findAccount(accountId);
			
			if (instance == null) {
				instance = buildAccount(configuration, accountId);
				insert(instance);
			}
			
			AccountEvent accountEvent = AccountEvent.builder()
					.userId(event.getPayload().getChangeEventHeader().getCommitUser())
					.timestamp(event.getPayload().getChangeEventHeader().getCommitTimestamp())
					.transactionKey(event.getPayload().getChangeEventHeader().getTransactionKey())
					.type(event.getPayload().getChangeEventHeader().getChangeType())
					.build();
			
			List<AccountEvent> events = new ArrayList<AccountEvent>(instance.getEvents());
			events.add(accountEvent);
			
			Account account = instance.toBuilder()
					.events(events)
					.isDeleted(Boolean.FALSE)
					.build();
			
			replace(account);
			
		} else {
			LOGGER.warn(String.format("Unsupported event type: %s", event.getPayload().getChangeEventHeader().getChangeType()));
		}
	}

	private void insert(Account account) {
		MongoConnection.getInstance().getDatabase().getCollection(ACCOUNTS, Account.class).insertOne(account);
	}
	
	private void replace(Account account) {
		MongoConnection.getInstance().getDatabase().getCollection(ACCOUNTS, Account.class).replaceOne(new Document("_id", account.getId()), account);
	}
	
	private Account findAccount(String id) {
		return MongoConnection.getInstance()
				.getDatabase()
				.getCollection("accounts", Account.class)
				.find(new Document("_id", id))
				.first();
	}
	
	private Account buildAccount(TopicConfiguration configuration, String accountId) throws SecureValueException {
		Token token = refreshToken(SecureValue.decryptBase64(configuration.getRefreshToken()));
		
		com.nowellpoint.client.sforce.model.Account source = SalesforceClientBuilder.defaultClient(token)
				.findById(com.nowellpoint.client.sforce.model.Account.class, accountId);
		
		return Account.of(configuration.getOrganizationId(), source);
	}
	
	private Token refreshToken(String refreshToken) {
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(SecretsManager.getSalesforceClientId())
				.setClientSecret(SecretsManager.getSalesforceClientSecret())
				.setRefreshToken(refreshToken)
				.build();
		
		OauthAuthenticationResponse response = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		return response.getToken();
    }
}
