package com.nowellpoint.api.rest.service;

import static com.nowellpoint.util.Assert.isEmpty;
import static com.nowellpoint.util.Assert.isEqual;
import static com.nowellpoint.util.Assert.isNotNull;
import static com.nowellpoint.util.Assert.isNull;

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

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;
import org.modelmapper.ModelMapper;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.braintreegateway.AddressRequest;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Result;
import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.exceptions.NotFoundException;
import com.nowellpoint.annotation.Deactivate;
import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.model.document.Photos;
import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.CreditCard;
import com.nowellpoint.api.rest.domain.IsoCountry;
import com.nowellpoint.api.rest.domain.Subscription;
import com.nowellpoint.api.rest.domain.Token;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.api.service.IsoCountryService;
import com.nowellpoint.api.service.PaymentGatewayService;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.util.Assert;

public class AccountProfileServiceImpl extends AbstractAccountProfileService implements AccountProfileService {
	
	private static final Logger LOGGER = Logger.getLogger(AccountProfileServiceImpl.class);
	
	protected static final ModelMapper modelMapper = new ModelMapper();
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Inject
	private PaymentGatewayService paymentGatewayService;
	
	@Inject
	private IsoCountryService isoCountryService;
	
	@Inject
	@Deactivate
	private Event<AccountProfile> deactivateEvent;
	
	public AccountProfileServiceImpl() {
		super();
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	public void loggedInEvent(@Observes Token token) {		
		UserContext.setUserContext(token.getAccessToken());
		
		String id = UserContext.getPrincipal().getName();
		
		AccountProfile accountProfile = AccountProfile.of(id);
		accountProfile.setLastLoginDate(Date.from(Instant.now()));
		
		updateAccountProfile( accountProfile );
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public void createAccountProfile(AccountProfile accountProfile) {
		accountProfile.setEnableSalesforceLogin(Boolean.FALSE);
		accountProfile.setIsActive(Boolean.FALSE);
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

		IsoCountry isoCountry = isoCountryService.findByIso2CodeAndLanguage(accountProfile.getAddress().getCountryCode(), "US");

		accountProfile.getAddress().setCountry(isoCountry.getDescription());

		Photos photos = new Photos();
		photos.setProfilePicture("/images/person-generic.jpg");

		accountProfile.setPhotos(photos);
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		accountProfile.setCreatedOn(now);
		accountProfile.setCreatedBy(userInfo);
		accountProfile.setLastUpdatedOn(now);
		accountProfile.setLastUpdatedBy(userInfo);
		
		create( accountProfile );
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public void deactivateAccountProfile(String id) {
		AccountProfile accountProfile = findById(id);
		accountProfile.setIsActive(Boolean.FALSE);
		accountProfile.setCreditCards(null);
		
		if (isNotNull(accountProfile.getAccountHref())) {
			identityProviderService.deactivateAccount(accountProfile.getAccountHref());
		}
		
		if (isNotNull(accountProfile.getSubscription())) {
			paymentGatewayService.cancelSubscription(accountProfile.getSubscription().getSubscriptionId());
		}
		
		updateAccountProfile( accountProfile );
		
		deactivateEvent.fire( accountProfile );
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public void updateAccountProfile(AccountProfile accountProfile) {
		
		AccountProfile original = findById( accountProfile.getId() );
		
		accountProfile.setEmailEncodingKey(original.getEmailEncodingKey());
		accountProfile.setHasFullAccess(original.getHasFullAccess());
		accountProfile.setCreatedBy(original.getCreatedBy());
		accountProfile.setCreatedOn(original.getCreatedOn());
		
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
		
		if (isNull(accountProfile.getMeta())) {
			accountProfile.setMeta(original.getMeta());
		}
		
		if (isNull(accountProfile.getAccountHref())) {
			accountProfile.setAccountHref(original.getAccountHref());
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
		
		if (isNull(accountProfile.getTransactions())) {
			accountProfile.setTransactions(original.getTransactions());
		}
		
		if (isNull(accountProfile.getHasFullAccess())) {
			accountProfile.setHasFullAccess(original.getHasFullAccess());
		}
		
		Date now = Date.from(Instant.now());
		
		accountProfile.setLastUpdatedOn(now);
		accountProfile.setUsername(accountProfile.getEmail());
		accountProfile.setName(accountProfile.getFirstName() != null ? accountProfile.getFirstName().concat(" ").concat(accountProfile.getLastName()) : accountProfile.getLastName());
		accountProfile.setLastUpdatedBy(new UserInfo(UserContext.getPrincipal().getName()));
		
		update(accountProfile);
		
		if (accountProfile.getIsActive() && isNotNull(accountProfile.getAccountHref())) {
			
			if (Assert.isNotEqual(accountProfile.getEmail(), original.getEmail()) ||
					Assert.isNotEqual(accountProfile.getFirstName(), original.getFirstName()) ||
					Assert.isNotEqual(accountProfile.getLastName(), original.getLastName())) {
				
				identityProviderService.updateAccount(
						accountProfile.getAccountHref(), 
						accountProfile.getEmail(), 
						accountProfile.getFirstName(), 
						accountProfile.getLastName());
			}
		}
		
		if (isNotNull(accountProfile.getSubscription())) {
			
			if (Assert.isNotEqual(accountProfile.getEmail(), original.getEmail()) ||
					Assert.isNotEqual(accountProfile.getFirstName(), original.getFirstName()) ||
					Assert.isNotEqual(accountProfile.getLastName(), original.getLastName()) ||
					Assert.isNotEqual(accountProfile.getCompany(), original.getCompany())) {
				
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
					LOGGER.error(e);
				}
				
				if (! customerResult.isSuccess()) {
					LOGGER.error(customerResult.getMessage());
				}
			}
		}
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public void updateAddress(String id, Address address) {
		AccountProfile accountProfile = findById( id );
		
		if (isEqual(address.getCountryCode(), accountProfile.getAddress().getCountryCode())) {
			address.setCountry(accountProfile.getAddress().getCountry());
		} else {
			IsoCountry isoCountry = isoCountryService.findByIso2CodeAndLanguage(address.getCountryCode(), "US");
			address.setCountry(isoCountry.getDescription());
		}
		
		accountProfile.setAddress(address);
		
		updateAccountProfile(accountProfile);
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public Subscription getSubscription(String id) {
		AccountProfile accountProfile = findById( id );
		return accountProfile.getSubscription();
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public void setSubscription(String accountProfileId, String paymentMethodToken, Subscription subscription) {
		AccountProfile accountProfile = findById( accountProfileId );
		
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
				LOGGER.error(subscriptionResult.getMessage());
				throw new ValidationException(subscriptionResult.getMessage());
			}
			
			subscription.setSubscriptionId(subscriptionResult.getTarget().getId());
		}
		
		subscription.setUpdatedOn(now);
		
		accountProfile.setSubscription(subscription);
		
		updateAccountProfile( accountProfile );	
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public Address getAddress(String id) {
		AccountProfile resource = findById( id );
		return resource.getAddress();
	}	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public AccountProfile findById(String id) {	
		return super.findById(id);
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public AccountProfile findByAccountHref(String accountHref) {
		return super.findByAccountHref(accountHref);
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public AccountProfile findByUsername(String username) {
		return super.findByUsername(username);
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public void addSalesforceProfilePicture(String userId, String profileHref) {
		
		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
		
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
	 * 
	 * 
	 */
	
	@Override
	public CreditCard getCreditCard(String id, String token) {
		AccountProfile resource = findById(id);
		
		Optional<CreditCard> creditCard = resource.getCreditCards()
				.stream()
				.filter(c -> token.equals(c.getToken()))
				.findFirst();
		
		return creditCard.get();
		
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public void addCreditCard(String id, CreditCard creditCard) {
		AccountProfile accountProfile = findById(id);
		
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
			creditCard.setCvv(null);
			
			creditCard.getBillingAddress().setCountry(addressResult.getTarget().getCountryName());
			
			accountProfile.addCreditCard(creditCard);
			
			updateAccountProfile( accountProfile );
			
		} else {
			LOGGER.error(creditCardResult.getMessage());
			throw new ValidationException(creditCardResult.getMessage());
		}
	}
	
	@Override
	public CreditCard setPrimary(String id, String token) {
		AccountProfile accountProfile = findById(id);
		
		Optional<CreditCard> query = accountProfile.getCreditCards()
				.stream()
				.filter(c -> token.equals(c.getToken()))
				.findFirst();
		
		CreditCard creditCard = null;
		
		if (query.isPresent()) {
			Date now = Date.from(Instant.now());
			
			creditCard = query.get();
			creditCard.setPrimary(Boolean.TRUE);
			creditCard.setUpdatedOn(now);
			
			accountProfile.getCreditCards().stream().filter(c -> ! c.getToken().equals(token)).forEach(c -> {
				if (c.getPrimary()) {
					c.setPrimary(Boolean.FALSE);
					c.setUpdatedOn(now);
				}
			});	
			
			accountProfile.setLastUpdatedOn(now);
			accountProfile.setLastUpdatedBy(new UserInfo(UserContext.getPrincipal().getName()));
			
			update(accountProfile);
		}
		
		return creditCard;
		
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public void updateCreditCard(String id, String token, CreditCard creditCard) {
		AccountProfile accountProfile = findById(id);
		
		CreditCardRequest creditCardRequest = new CreditCardRequest()
				.cardholderName(creditCard.getCardholderName())
				.expirationMonth(creditCard.getExpirationMonth())
				.expirationYear(creditCard.getExpirationYear());
		
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
				accountProfile.getCreditCards().stream().filter(c -> ! c.getToken().equals(null)).forEach(c -> {
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
			creditCard.setCvv(null);
			
			if (Assert.isNull(creditCard.getPrimary())) {
				creditCard.setPrimary(original.getPrimary());
			}
			
			if (Assert.isNull(creditCard.getBillingAddress())) {
				creditCard.setBillingAddress(original.getBillingAddress());
			}
			
			if (Assert.isNull(creditCard.getBillingContact())) {
				creditCard.setBillingContact(original.getBillingContact());
			}
			
			if (Assert.isNull(creditCard.getCardholderName())) {
				creditCard.setCardholderName(original.getCardholderName());
			}
			
			if (Assert.isNull(creditCard.getCardType())) {
				creditCard.setCardType(original.getCardType());
			}
			
			if (Assert.isNull(creditCard.getExpirationMonth())) {
				creditCard.setExpirationMonth(original.getExpirationMonth());
			}
			
			if (Assert.isNull(creditCard.getExpirationYear())) {
				creditCard.setExpirationYear(original.getExpirationYear());
			}
			
			if (Assert.isNull(creditCard.getPrimary())) {
				creditCard.setPrimary(original.getPrimary());
			}
			
			if (addressResult.isSuccess()) {
				creditCard.getBillingAddress().setCountry(addressResult.getTarget().getCountryName());
			}
			
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
	 * 
	 * 
	 */
	
	@Override
	public void removeCreditCard(String id, String token) {
		
		AccountProfile accountProfile = findById(id);
		
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