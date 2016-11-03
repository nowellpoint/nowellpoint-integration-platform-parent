package com.nowellpoint.api.service;

import static com.nowellpoint.util.Assert.isEmpty;
import static com.nowellpoint.util.Assert.isNull;
import static com.nowellpoint.util.Assert.isEqual;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.braintreegateway.AddressRequest;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Result;
import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.exceptions.NotFoundException;
import com.nowellpoint.api.dto.idp.Token;
import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.model.document.IsoCountry;
import com.nowellpoint.api.model.document.Photos;
import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.model.dto.CreditCard;
import com.nowellpoint.api.model.dto.Subscription;
import com.nowellpoint.api.model.dto.UserInfo;
import com.nowellpoint.api.model.mapper.AccountProfileModelMapper;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.util.Assert;

public class AccountProfileService extends AccountProfileModelMapper {
	
	private static final Logger LOGGER = Logger.getLogger(AccountProfileService.class);
	
	@Inject
	private PaymentGatewayService paymentGatewayService;
	
	@Inject
	private IsoCountryService isoCountryService;
	
	public AccountProfileService() {
		super();
	}
	
	/**
	 * 
	 * 
	 * @param token
	 * 
	 * 
	 */
	
	public void loggedInEvent(@Observes Token token) {		
		UserContext.setUserContext(token.getAccessToken());
		
		String id = UserContext.getPrincipal().getName();
		
		AccountProfile accountProfile = new AccountProfile(id);
		accountProfile.setLastLoginDate(Date.from(Instant.now()));
		
		updateAccountProfile( accountProfile );
	}
	
	/**
	 * 
	 * 
	 * @param accountProfile
	 * 
	 * 
	 */
	
	public void createAccountProfile(AccountProfile accountProfile) {
		accountProfile.setEnableSalesforceLogin(Boolean.FALSE);
		accountProfile.setUsername(accountProfile.getEmail());
		accountProfile.setName(accountProfile.getFirstName() != null ? accountProfile.getFirstName().concat(" ").concat(accountProfile.getLastName()) : accountProfile.getLastName());

		if (isNull(accountProfile.getLocaleSidKey())) {
			accountProfile.setLocaleSidKey(Locale.getDefault().toString());
		}

		if (isNull(accountProfile.getLanguageSidKey())) {
			accountProfile.setLanguageSidKey(Locale.getDefault().toString());
		}

		if (isNull(accountProfile.getTimeZoneSidKey())) {
			accountProfile.setTimeZoneSidKey(TimeZone.getDefault().getID());
		}

		IsoCountry isoCountry = isoCountryService.lookupByIso2Code(accountProfile.getAddress().getCountryCode(), "US");

		accountProfile.getAddress().setCountry(isoCountry.getDescription());

		Photos photos = new Photos();
		photos.setProfilePicture("/images/person-generic.jpg");

		accountProfile.setPhotos(photos);
		
		UserInfo userInfo = new UserInfo(getSubject());
		
		accountProfile.setCreatedBy(userInfo);
		accountProfile.setLastModifiedBy(userInfo);

		super.createAccountProfile(accountProfile);
		
		CustomerRequest customerRequest = new CustomerRequest()
				.id(accountProfile.getId())
				.company(accountProfile.getCompany())
				.email(accountProfile.getEmail())
				.firstName(accountProfile.getFirstName())
				.lastName(accountProfile.getLastName())
				.phone(accountProfile.getPhone());
		
		Result<com.braintreegateway.Customer> customerResult = null;
		
		try {
			customerResult = paymentGatewayService.addOrUpdateCustomer(customerRequest);
		} catch (NotFoundException e) {
			throw new ValidationException(e.getMessage());
		}
		
		if (! customerResult.isSuccess()) {
			LOGGER.error(customerResult.getMessage());
		}
	}
	
	public void deactivateAccountProfile(AccountProfile accountProfile) {
		accountProfile.setIsActive(Boolean.FALSE);
		
		updateAccountProfile( accountProfile );
	}
	
	/**
	 * 
	 * @param subject
	 * @param accountProfile
	 * @param eventSource
	 * @return the updated Identity resource
	 */
	
	public void updateAccountProfile(AccountProfile accountProfile) {
		AccountProfile original = findAccountProfile( accountProfile.getId() );
		
		accountProfile.setEmailEncodingKey(original.getEmailEncodingKey());
		accountProfile.setHasFullAccess(original.getHasFullAccess());
		accountProfile.setCreatedBy(original.getCreatedBy());
		accountProfile.setCreatedDate(original.getCreatedDate());
		accountProfile.setSystemCreatedDate(original.getSystemCreatedDate());
		accountProfile.setLeadId(original.getLeadId());
		
		if (isNull(accountProfile.getDivision())) {
			accountProfile.setDivision(original.getDivision());
		} else if (isEmpty(accountProfile.getDivision())) {
			accountProfile.setDivision(null);
		}
		
		if (isNull(accountProfile.getFirstName())) {
			accountProfile.setFirstName(original.getFirstName());
		} else if (isEmpty(accountProfile.getFirstName())) {
			accountProfile.setFirstName(null);
		}
		
		if (isNull(accountProfile.getCompany())) {
			accountProfile.setCompany(original.getCompany());
		} else if (isEmpty(accountProfile.getCompany())) {
			accountProfile.setCompany(null);
		}
		
		if (isNull(accountProfile.getDepartment())) {
			accountProfile.setDepartment(original.getDepartment());
		} else if (isEmpty(accountProfile.getDepartment())) {
			accountProfile.setDepartment(null);
		}
		
		if (isNull(accountProfile.getTitle())) {
			accountProfile.setTitle(original.getTitle());
		} else if (isEmpty(accountProfile.getTitle())) {
			accountProfile.setTitle(null);
		}
		
		if (isNull(accountProfile.getFax())) {
			accountProfile.setFax(original.getFax());
		} else if (isEmpty(accountProfile.getFax())) {
			accountProfile.setFax(null);
		}
		
		if (isNull(accountProfile.getMobilePhone())) {
			accountProfile.setMobilePhone(original.getMobilePhone());
		} else if (isEmpty(accountProfile.getMobilePhone())) {
			accountProfile.setMobilePhone(null);
		}
		
		if (isNull(accountProfile.getPhone())) {
			accountProfile.setPhone(original.getPhone());
		} else if (isEmpty(accountProfile.getPhone())) {
			accountProfile.setPhone(null);
		}
		
		if (isNull(accountProfile.getExtension())) {
			accountProfile.setExtension(original.getExtension());
		} else if (isEmpty(accountProfile.getExtension())) {
			accountProfile.setExtension(null);
		}
		
		if (isNull(accountProfile.getLastName())) {
			accountProfile.setLastName(original.getLastName());
		}
		
		if (isNull(accountProfile.getEmail())) {
			accountProfile.setEmail(original.getEmail());
		}
		
		if (isNull(accountProfile.getHref())) {
			accountProfile.setHref(original.getHref());
		}
		
		if (isNull(accountProfile.getLocaleSidKey())) {
			accountProfile.setLocaleSidKey(original.getLocaleSidKey());
		}

		if (isNull(accountProfile.getLanguageSidKey())) {
			accountProfile.setLanguageSidKey(original.getLanguageSidKey());
		}

		if (isNull(accountProfile.getTimeZoneSidKey())) {
			accountProfile.setTimeZoneSidKey(original.getTimeZoneSidKey());
		}
		
		if (isNull(accountProfile.getEnableSalesforceLogin())) {
			accountProfile.setEnableSalesforceLogin(original.getEnableSalesforceLogin());
		}
		
		if (isNull(accountProfile.getAddress())) {
			accountProfile.setAddress(original.getAddress());
		}
		
		if (isNull(accountProfile.getLastLoginDate())) {
			accountProfile.setLastLoginDate(original.getLastLoginDate());
		}
		
		if (isNull(accountProfile.getPhotos())) {
			accountProfile.setPhotos(original.getPhotos());
		}
		
		if (isNull(accountProfile.getCreditCards())) {
			accountProfile.setCreditCards(original.getCreditCards());
		}
		
		if (isNull(accountProfile.getIsActive())) {
			accountProfile.setIsActive(original.getIsActive());
		}
		
		if (isNull(accountProfile.getSubscription())) {
			accountProfile.setSubscription(original.getSubscription());
		}
		
		if (isNull(accountProfile.getHasFullAccess())) {
			accountProfile.setHasFullAccess(original.getHasFullAccess());
		}
		
		accountProfile.setUsername(accountProfile.getEmail());
		accountProfile.setName(accountProfile.getFirstName() != null ? accountProfile.getFirstName().concat(" ").concat(accountProfile.getLastName()) : accountProfile.getLastName());
		accountProfile.setLastModifiedBy(new UserInfo(getSubject()));
		
		super.updateAccountProfile(accountProfile);
		
		CustomerRequest customerRequest = new CustomerRequest()
				.id(accountProfile.getId())
				.company(accountProfile.getCompany())
				.email(accountProfile.getEmail())
				.firstName(accountProfile.getFirstName())
				.lastName(accountProfile.getLastName())
				.phone(accountProfile.getPhone());
		
		Result<com.braintreegateway.Customer> customerResult = null;
		
		try {
			customerResult = paymentGatewayService.addOrUpdateCustomer(customerRequest);
		} catch (NotFoundException e) {
			throw new ValidationException(e.getMessage());
		}
		
		if (! customerResult.isSuccess()) {
			LOGGER.error(customerResult.getMessage());
		}
	}
	
	/**
	 * 
	 * 
	 * @param subject
	 * @param id
	 * @param address
	 * @return
	 * 
	 * 
	 */
	
	public void updateAddress(String id, Address address) {
		AccountProfile accountProfile = findAccountProfile( id );
		
		
		if (isEqual(address.getCountryCode(), accountProfile.getAddress().getCountryCode())) {
			address.setCountry(accountProfile.getAddress().getCountry());
		} else {
			IsoCountry isoCountry = isoCountryService.lookupByIso2Code(address.getCountryCode(), "US");
			address.setCountry(isoCountry.getDescription());
		}
		
		accountProfile.setAddress(address);
		
		super.updateAccountProfile(accountProfile);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */
	
	public Subscription getSubscription(String id) {
		AccountProfile accountProfile = findAccountProfile( id );
		return accountProfile.getSubscription();
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param subscription
	 * 
	 * 
	 */
	
	public void setSubscription(String accountProfileId, String paymentMethodToken, Subscription subscription) {
		AccountProfile accountProfile = findAccountProfile( accountProfileId );
		
		Date now = Date.from(Instant.now());
		
		if (subscription.getUnitPrice() == 0 && accountProfile.getSubscription() == null) {
			subscription.setAddedOn(now);
		} else {
			
			if (subscription.getUnitPrice() > 0 && accountProfile.getPrimaryCreditCard() == null) {
				throw new ValidationException("Unable to process subscription request because there is no credit card associated with the account profile");
			}
			
			Result<com.braintreegateway.Subscription> subscriptionResult = null;
			
			if (Assert.isNull(accountProfile.getSubscription()) || Assert.isNull(accountProfile.getSubscription().getSubscriptionId())) {
				
				SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
					    .paymentMethodToken(paymentMethodToken)
					    .planId(subscription.getPlanCode())
					    .price(new BigDecimal(subscription.getUnitPrice()));

				subscriptionResult = paymentGatewayService.createSubscription(subscriptionRequest);
				
				subscription.setAddedOn(now);
				
			} else {
				
				SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
					    .paymentMethodToken(paymentMethodToken)
					    .planId(subscription.getPlanCode())
					    .price(new BigDecimal(subscription.getUnitPrice()));

				try {
					subscriptionResult = paymentGatewayService.updateSubscription(accountProfile.getSubscription().getSubscriptionId(), subscriptionRequest);
				} catch (NotFoundException e) {
					throw new ValidationException(e.getMessage());
				}
				
				subscription.setAddedOn(accountProfile.getSubscription().getAddedOn());
			}
			
			if (! subscriptionResult.isSuccess()) {
				LOGGER.error(subscriptionResult.getErrors().forIndex(0).toString());
				throw new ValidationException(subscriptionResult.getMessage());
			}
			
			subscription.setSubscriptionId(subscriptionResult.getTarget().getId());
		}
		
		subscription.setUpdatedOn(now);
		
		accountProfile.setSubscription(subscription);
		
		super.updateAccountProfile(accountProfile);
	}
	
	/**
	 * 
	 * 
	 * @param subject
	 * @param id
	 * @return
	 * 
	 * 
	 */
	
	public Address getAddress(String id) {
		AccountProfile resource = findAccountProfile( id );
		return resource.getAddress();
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return Identity resource for id
	 * 
	 * 
	 */
	
	public AccountProfile findAccountProfile(String id) {	
		return super.findAccountProfile(id);
	}
	
	/**
	 * 
	 * 
	 * @param href
	 * @return AccountProfile resource for subject
	 * 
	 * 
	 */
	
	public AccountProfile findAccountProfileByHref(String href) {
		return super.findAccountProfileByHref(href);
	}
	
	/**
	 * 
	 * 
	 * @param username
	 * @return AccountProfile resource for subject
	 * 
	 * 
	 */
	
	public AccountProfile findAccountProfileByUsername(String username) {
		return super.findAccountProfileByUsername(username);
	}
	
	/**
	 * 
	 * 
	 * @param userId
	 * @param profileHref
	 * 
	 * 
	 */
	
	public void addSalesforceProfilePicture(String userId, String profileHref) {
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		try {
			URL url = new URL( profileHref );
			
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			String contentType = connection.getHeaderField("Content-Type");
			
			ObjectMetadata objectMetadata = new ObjectMetadata();
	    	objectMetadata.setContentLength(connection.getContentLength());
	    	objectMetadata.setContentType(contentType);
			
	    	PutObjectRequest putObjectRequest = new PutObjectRequest("aws-microservices", userId, connection.getInputStream(), objectMetadata);
	    	
	    	s3Client.putObject(putObjectRequest);
			
		} catch (IOException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param token
	 * @return CreditCard
	 * 
	 * 
	 */
	
	public CreditCard getCreditCard(String id, String token) {
		AccountProfile resource = findAccountProfile(id);
		
		Optional<CreditCard> creditCard = resource.getCreditCards()
				.stream()
				.filter(c -> token.equals(c.getToken()))
				.findFirst();
		
		return creditCard.get();
		
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param creditCard
	 * 
	 * 
	 */
	
	public void addCreditCard(String id, CreditCard creditCard) {
		AccountProfile accountProfile = findAccountProfile(id);
		
		if (! Assert.isNumber(creditCard.getExpirationMonth())) {
			throw new ValidationException("Unable to process credit card because it has an invalid month: " + creditCard.getExpirationMonth()); 
		}
		
		if (! Assert.isNumber(creditCard.getExpirationYear())) {
			throw new ValidationException("Unable to process credit card because it has an invalid year: " + creditCard.getExpirationYear()); 
		}
		
		Integer month = Integer.valueOf(creditCard.getExpirationMonth());
		Integer year = Integer.valueOf(creditCard.getExpirationYear());
		
		if (month < 1 || month > 12) {
			throw new ValidationException("Unable to process credit card because it has an invalid month: " + creditCard.getExpirationMonth()); 
		}
		
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"));
		
		if (year < dateTime.getYear()) {
			throw new ValidationException("Unable to process credit card because it has an invalid year: " + year);
		}
		
		AddressRequest addressRequest = new AddressRequest()
				.countryCodeAlpha2(creditCard.getBillingAddress().getCountryCode())
				.firstName(creditCard.getBillingContact().getFirstName())
				.lastName(creditCard.getBillingContact().getLastName())
				.locality(creditCard.getBillingAddress().getCity())
				.region(creditCard.getBillingAddress().getState())
				.postalCode(creditCard.getBillingAddress().getPostalCode())
				.streetAddress(creditCard.getBillingAddress().getStreet());
		
		Result<com.braintreegateway.Address> addressResult = paymentGatewayService.createAddress(accountProfile.getId(), addressRequest);
		
		CreditCardRequest creditCardRequest = new CreditCardRequest()
				.cardholderName(creditCard.getCardholderName())
				.expirationMonth(creditCard.getExpirationMonth())
				.expirationYear(creditCard.getExpirationYear())
				.number(creditCard.getNumber())
				.cvv(creditCard.getCvv())
				.customerId(accountProfile.getId())
				.billingAddressId(addressResult.getTarget().getId());
		
		Result<com.braintreegateway.CreditCard> creditCardResult = paymentGatewayService.createCreditCard(creditCardRequest);
		
		if (creditCardResult.isSuccess()) {
			
			if (accountProfile.getCreditCards() == null || accountProfile.getCreditCards().size() == 0) {
				creditCard.setPrimary(Boolean.TRUE);
			} else if (creditCard.getPrimary()) {
				accountProfile.getCreditCards().stream().filter(c -> ! c.getToken().equals(null)).forEach(c -> {
					if (c.getPrimary()) {
						c.setPrimary(Boolean.FALSE);
					}
				});			
			} else {
				creditCard.setPrimary(Boolean.FALSE);
			}
			
			creditCard.setNumber(creditCardResult.getTarget().getMaskedNumber());
			creditCard.setToken(creditCardResult.getTarget().getToken());
			creditCard.setImageUrl(creditCardResult.getTarget().getImageUrl());
			creditCard.setLastFour(creditCardResult.getTarget().getLast4());
			creditCard.setCardType(creditCardResult.getTarget().getCardType());
			creditCard.setAddedOn(Date.from(Instant.now()));
			creditCard.setUpdatedOn(Date.from(Instant.now()));
			
			creditCard.getBillingAddress().setCountry(addressResult.getTarget().getCountryName());
			
			accountProfile.addCreditCard(creditCard);
			
			updateAccountProfile( accountProfile );
			
		} else {
			LOGGER.error(creditCardResult.getMessage());
			throw new ValidationException(creditCardResult.getMessage());
		}
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param token
	 * @param creditCard
	 * 
	 * 
	 */
	
	public void updateCreditCard(String id, String token, CreditCard creditCard) {
		AccountProfile accountProfile = findAccountProfile(id);
		
		CreditCardRequest creditCardRequest = new CreditCardRequest()
				.cardholderName(creditCard.getCardholderName())
				.expirationMonth(creditCard.getExpirationMonth())
				.expirationYear(creditCard.getExpirationYear())
				.number(creditCard.getNumber());
		
		Result<com.braintreegateway.CreditCard> creditCardResult = null;
		
		try {
			creditCardResult = paymentGatewayService.updateCreditCard(token, creditCardRequest);
		} catch (NotFoundException e) {
			throw new ValidationException(e.getMessage());
		}
		
		if (creditCardResult.isSuccess()) {
			
			AddressRequest addressRequest = new AddressRequest()
					.countryCodeAlpha2(creditCard.getBillingAddress().getCountryCode())
					.firstName(creditCard.getBillingContact().getFirstName())
					.lastName(creditCard.getBillingContact().getLastName())
					.locality(creditCard.getBillingAddress().getCity())
					.region(creditCard.getBillingAddress().getState())
					.postalCode(creditCard.getBillingAddress().getPostalCode())
					.streetAddress(creditCard.getBillingAddress().getStreet());
			
			Result<com.braintreegateway.Address> addressResult = null;
			
			try {
				addressResult = paymentGatewayService.updateAddress(
						creditCardResult.getTarget().getCustomerId(), 
						creditCardResult.getTarget().getBillingAddress().getId(), 
						addressRequest);
			} catch (NotFoundException e) {
				throw new ValidationException(e.getMessage());
			}
			
			if (creditCard.getPrimary()) {
				accountProfile.getCreditCards().stream().filter(c -> ! c.getToken().equals(token)).forEach(c -> {
					if (c.getPrimary()) {
						c.setPrimary(Boolean.FALSE);
					}
				});			
			}
			
			CreditCard original = accountProfile.getCreditCards()
					.stream()
					.filter(c -> token.equals(c.getToken()))
					.findFirst()
					.get();
			
			creditCard.setAddedOn(original.getAddedOn());
			creditCard.setNumber(original.getNumber());
			creditCard.setLastFour(original.getLastFour());
			creditCard.setCardType(original.getCardType());
			creditCard.setImageUrl(original.getImageUrl());
			creditCard.setToken(original.getToken());
			creditCard.setUpdatedOn(Date.from(Instant.now()));
			creditCard.getBillingAddress().setCountry(addressResult.getTarget().getCountryName());
			
			accountProfile.getCreditCards().removeIf(c -> token.equals(c.getToken()));
			
			accountProfile.addCreditCard(creditCard);
			
			updateAccountProfile( accountProfile );
			
		} else {
			LOGGER.error(creditCardResult.getMessage());
			throw new ValidationException(creditCardResult.getMessage());
		}
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param token
	 * @param parameters
	 * @return
	 * 
	 * 
	 */
	
	public CreditCard updateCreditCard(String id, String token, MultivaluedMap<String,String> parameters) {
		
		CreditCard creditCard = getCreditCard(id, token);
		
		if (parameters.containsKey("cardholderName")) {
			creditCard.setCardholderName(parameters.getFirst("cardholderName"));
		}
		
		if (parameters.containsKey("expirationMonth")) {
			creditCard.setExpirationMonth(parameters.getFirst("expirationMonth"));
		}
		
		if (parameters.containsKey("expirationYear")) {
			creditCard.setExpirationYear(parameters.getFirst("expirationYear"));
		}
		
		if (parameters.containsKey("primary")) {
			creditCard.setPrimary(Boolean.valueOf(parameters.getFirst("primary")));
		}
		
		updateCreditCard(id, token, creditCard);
		
		return creditCard;
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param token
	 * 
	 * 
	 */
	
	public void removeCreditCard(String id, String token) {
		
		AccountProfile accountProfile = findAccountProfile(id);
		
		if (token.equals(accountProfile.getPrimaryCreditCard().getToken())) {
			throw new ValidationException("Unable to delete credit card because it has been set as the primary card for the account profile.");
		}
		
		com.braintreegateway.CreditCard creditCard = paymentGatewayService.findCreditCard(token);
		
		Result<com.braintreegateway.CreditCard> creditCardResult = paymentGatewayService.deleteCreditCard(token);
		
		if (creditCardResult.isSuccess()) {
			paymentGatewayService.deleteAddress(creditCard.getCustomerId(), creditCard.getBillingAddress().getId());
			accountProfile.getCreditCards().removeIf(c -> token.equals(c.getToken()));
			updateAccountProfile( accountProfile );
		} else {
			LOGGER.error(creditCardResult.getMessage());
		}
	}
}