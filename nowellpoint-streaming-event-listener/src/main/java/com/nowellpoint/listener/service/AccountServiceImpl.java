package com.nowellpoint.listener.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.json.bind.config.PropertyVisibilityStrategy;

import org.bson.Document;
import org.jboss.logging.Logger;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
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
import com.nowellpoint.listener.model.Address;
import com.nowellpoint.listener.model.AddressComponent;
import com.nowellpoint.listener.model.GeoCodedAddress;
import com.nowellpoint.listener.model.TopicConfiguration;
import com.nowellpoint.util.SecretsManager;
import com.nowellpoint.util.SecureValue;
import com.nowellpoint.util.SecureValueException;

public class AccountServiceImpl implements AccountService {
	
	@Inject
    private Event<Account> fireAlarm;
	
	private static final Logger LOGGER = Logger.getLogger(AccountServiceImpl.class);
	private static final String ACCOUNTS = "accounts";
	private static final String ADDRESSES = "addresses";
	private static final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig()
			.withNullValues(Boolean.TRUE)
			.withPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE)
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
	
	@Override
	public void processChangeEvent(ChangeEvent event, TopicConfiguration configuration) throws SecureValueException, ApiException, InterruptedException, IOException {
		
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
			
			fireAlarm.fireAsync(account);
			
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
	
	private void save(GeoCodedAddress address) {
		LOGGER.info("1: " + address.getCompoundCode());
		LOGGER.info("2: " + address.getId());
		UpdateResult result = MongoConnection.getInstance().getDatabase().getCollection(ADDRESSES, GeoCodedAddress.class).replaceOne(new Document("_id", address.getId()), address, new ReplaceOptions().upsert(true));
		LOGGER.info(result.toString());
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
	
	private GeoCodedAddress geoCodeAddress(Address address) throws ApiException, InterruptedException, IOException {
		if (address == null)
			return null;
		
		String addressString = new StringBuilder()
				.append(address.getStreet())
				.append(" ")
				.append(address.getCity())
				.append(", ")
				.append(address.getState())
				.append(" ")
				.append(address.getPostalCode())
				.append(" ")
				.append(address.getCountryCode())
				.toString();
		
		GeoApiContext context = new GeoApiContext.Builder()
				.apiKey(System.getenv("GOOGLE_API_KEY"))
			    .build();
		
		GeocodingResult[] result = GeocodingApi.geocode(context, addressString).await();
		
		if (result != null) {
			return GeoCodedAddress.builder()
					.addressComponents(Arrays.stream(result[0].addressComponents)
							.map(ac -> AddressComponent.builder()
									.longName(ac.longName)
									.shortName(ac.shortName)
									.types(Arrays.stream(ac.types)
											.map(t -> t.name())
											.collect(Collectors.toList()))
									.build())
							.collect(Collectors.toList()))
					.formattedAddress(result[0].formattedAddress)
					.latitude(result[0].geometry.location.lat)
					.longitude(result[0].geometry.location.lng)
					.partialMatch(result[0].partialMatch)
					.id(result[0].placeId)
					.globalCode(result[0].plusCode.globalCode)
					.compoundCode(result[0].plusCode.compoundCode)
					.build();
		} else {
			return null;
		}
	}
	
	public void onFireNotification(@ObservesAsync Account account) {
		LOGGER.info("Here is the event: " + account.getId());
		try {
			GeoCodedAddress billingAddress = geoCodeAddress(account.getBillingAddress());
			if (billingAddress != null) {
				save(billingAddress);
			}
			GeoCodedAddress shippingAddress = geoCodeAddress(account.getShippingAddress());
			if (shippingAddress != null) {
				save(shippingAddress);
			}
		} catch (ApiException | InterruptedException | IOException e) {
			e.printStackTrace();
		}
		
	}
}
