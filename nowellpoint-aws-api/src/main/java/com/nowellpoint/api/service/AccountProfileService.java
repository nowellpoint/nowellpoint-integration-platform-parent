package com.nowellpoint.api.service;

import static com.nowellpoint.util.Assert.isNull;
import static com.nowellpoint.util.Assert.isNotNull;
import static com.nowellpoint.util.Assert.isNotEqual;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.braintreegateway.AddressRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.exceptions.NotFoundException;
import com.nowellpoint.api.dto.idp.Token;
import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.model.document.IsoCountry;
import com.nowellpoint.api.model.document.Photos;
import com.nowellpoint.api.model.document.SystemReference;
import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.model.dto.CreditCardDTO;
import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.mapper.AccountProfileModelMapper;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.aws.model.admin.Properties;

public class AccountProfileService extends AccountProfileModelMapper {
	
	private static final Logger LOGGER = Logger.getLogger(AccountProfileService.class);
	
	@Inject
	private IsoCountryService isoCountryService;
	
	private static BraintreeGateway gateway = new BraintreeGateway(
			Environment.parseEnvironment(System.getProperty(Properties.BRAINTREE_ENVIRONMENT)),
			System.getProperty(Properties.BRAINTREE_MERCHANT_ID),
			System.getProperty(Properties.BRAINTREE_PUBLIC_KEY),
			System.getProperty(Properties.BRAINTREE_PRIVATE_KEY)
	);
	
	static {
		gateway.clientToken().generate();
	}
	
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
		
		String subject = UserContext.getPrincipal().getName();
		
		Id id = new Id( subject );
		
		AccountProfile accountProfile = findAccountProfile( id );
		accountProfile.setLastLoginDate(Date.from(Instant.now()));
		
		updateAccountProfile( id, accountProfile);
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
		
		AccountProfile createdBy = new AccountProfile(getSubject());
		
		accountProfile.setCreatedBy(createdBy);
		accountProfile.setLastModifiedBy(createdBy);

		super.createAccountProfile(accountProfile);
	}
	
	/**
	 * 
	 * @param subject
	 * @param accountProfile
	 * @param eventSource
	 * @return the updated Identity resource
	 */
	
	public void updateAccountProfile(Id id, AccountProfile accountProfile) {
		AccountProfile original = findAccountProfile( id );
		
		accountProfile.setId( id );
		accountProfile.setName(accountProfile.getFirstName() != null ? accountProfile.getFirstName().concat(" ").concat(accountProfile.getLastName()) : accountProfile.getLastName());
		accountProfile.setCreatedById(original.getCreatedById());
		accountProfile.setCreatedDate(original.getCreatedDate());
		accountProfile.setHref(original.getHref());
		accountProfile.setEmailEncodingKey(original.getEmailEncodingKey());
		accountProfile.setIsActive(original.getIsActive());
		accountProfile.setHasFullAccess(original.getHasFullAccess());
		
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
		
		accountProfile.setLastModifiedBy(new AccountProfile(getSubject()));
		
		super.updateAccountProfile(accountProfile);
	}
	
	/**
	 * 
	 * @param subject
	 * @param id
	 * @param address
	 * @return
	 */
	
	public void updateAccountProfileAddress(Id id, Address address) {
		AccountProfile accountProfile = findAccountProfile( id );
		
		if (isNotEqual(address.getCountryCode(), accountProfile.getAddress().getCountryCode())) {
			IsoCountry isoCountry = isoCountryService.lookupByIso2Code(address.getCountryCode(), "US");
			address.setCountry(isoCountry.getDescription());
		}
		
		accountProfile.setAddress(address);
		
		super.updateAccountProfile(accountProfile);
	}
	
	/**
	 * 
	 * @param subject
	 * @param id
	 * @return
	 */
	
	public Address getAccountProfileAddress(Id id) {
		AccountProfile resource = findAccountProfile( id );
		return resource.getAddress();
	}
	
	/**
	 * 
	 * @param id
	 * @return Identity resource for id
	 */
	
	public AccountProfile findAccountProfile(Id id) {		
		return super.findAccountProfile(id);
	}
	
	/**
	 * 
	 * @param subject
	 * @return Identity resource for subject
	 */
	
	public AccountProfile findAccountProfileByHref(String href) {
		return super.findAccountProfileByHref(href);
	}
	
	public AccountProfile findAccountProfileByUsername(String username) {
		return super.findAccountProfileByUsername(username);
	}
	
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
	
	public CreditCardDTO getCreditCard(Id id, String token) {
		AccountProfile resource = findAccountProfile(id);
		
		Optional<CreditCardDTO> creditCard = resource.getCreditCards()
				.stream()
				.filter(c -> token.equals(c.getToken()))
				.findFirst();
		
		return creditCard.get();
		
	}
	
	public void addCreditCard(Id id, CreditCardDTO creditCard) {
		AccountProfile resource = findAccountProfile(id);
		
		CustomerRequest customerRequest = new CustomerRequest()
				.company(resource.getCompany())
				.email(resource.getEmail())
				.firstName(resource.getFirstName())
				.lastName(resource.getLastName())
				.phone(resource.getPhone());
		
		Result<Customer> customerResult = null;
		
		if (isNotNull(resource.getSystemReferences())) {
			
			Optional<SystemReference> optional = resource.getSystemReferences()
					.stream()
					.filter(s -> "BRAINTREE".equals(s.getSystem()))
					.findFirst();
			
			if (optional.isPresent()) {
				try {
					Customer customer = gateway.customer().find(optional.get().getSystemReference());
					customerResult = gateway.customer().update(customer.getId(), customerRequest);
				} catch (NotFoundException e) {
					LOGGER.warn(e.getMessage());
				}
			}
		}
		
		if (customerResult == null) {
			customerResult = gateway.customer().create(customerRequest);
			
			SystemReference systemReference = new SystemReference();
			systemReference.setSystem("BRAINTREE");
			systemReference.setSystemReference(customerResult.getTarget().getId());
			
			resource.addSystemReference(systemReference);
		}
		
		AddressRequest addressRequest = new AddressRequest()
				.countryCodeAlpha2(creditCard.getBillingAddress().getCountryCode())
				.firstName(creditCard.getBillingContact().getFirstName())
				.lastName(creditCard.getBillingContact().getLastName())
				.locality(creditCard.getBillingAddress().getCity())
				.region(creditCard.getBillingAddress().getState())
				.postalCode(creditCard.getBillingAddress().getPostalCode())
				.streetAddress(creditCard.getBillingAddress().getStreet());
		
		Result<com.braintreegateway.Address> addressResult = gateway.address().create(customerResult.getTarget().getId(), addressRequest);
		
		CreditCardRequest creditCardRequest = new CreditCardRequest()
				.cardholderName(creditCard.getCardholderName())
				.expirationMonth(creditCard.getExpirationMonth())
				.expirationYear(creditCard.getExpirationYear())
				.number(creditCard.getNumber())
				.customerId(customerResult.getTarget().getId())
				.billingAddressId(addressResult.getTarget().getId());
		
		Result<com.braintreegateway.CreditCard> creditCardResult = gateway.creditCard().create(creditCardRequest);
		
		if (creditCardResult.isSuccess()) {
			
			if (resource.getCreditCards() == null || resource.getCreditCards().size() == 0) {
				creditCard.setPrimary(Boolean.TRUE);
			} else if (creditCard.getPrimary()) {
				resource.getCreditCards().stream().filter(c -> ! c.getToken().equals(null)).forEach(c -> {
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
			
			resource.addCreditCard(creditCard);
			
			updateAccountProfile(id, resource);
			
		} else {
			LOGGER.error(creditCardResult.getMessage());
			throw new ServiceException(creditCardResult.getMessage());
		}
	}
	
	public void updateCreditCard(Id id, String token, CreditCardDTO creditCard) {
		AccountProfile resource = findAccountProfile(id);
		
		CreditCardRequest creditCardRequest = new CreditCardRequest()
				.cardholderName(creditCard.getCardholderName())
				.expirationMonth(creditCard.getExpirationMonth())
				.expirationYear(creditCard.getExpirationYear())
				.number(creditCard.getNumber());
		
		Result<com.braintreegateway.CreditCard> creditCardResult = gateway.creditCard().update(token, creditCardRequest);
		
		if (creditCardResult.isSuccess()) {
			
			AddressRequest addressRequest = new AddressRequest()
					.countryCodeAlpha2(creditCard.getBillingAddress().getCountryCode())
					.firstName(creditCard.getBillingContact().getFirstName())
					.lastName(creditCard.getBillingContact().getLastName())
					.locality(creditCard.getBillingAddress().getCity())
					.region(creditCard.getBillingAddress().getState())
					.postalCode(creditCard.getBillingAddress().getPostalCode())
					.streetAddress(creditCard.getBillingAddress().getStreet());
			
			Result<com.braintreegateway.Address> addressResult = gateway.address().update(
					creditCardResult.getTarget().getCustomerId(), 
					creditCardResult.getTarget().getBillingAddress().getId(), 
					addressRequest);
			
			if (creditCard.getPrimary()) {
				resource.getCreditCards().stream().filter(c -> ! c.getToken().equals(token)).forEach(c -> {
					if (c.getPrimary()) {
						c.setPrimary(Boolean.FALSE);
					}
				});			
			}
			
			CreditCardDTO original = resource.getCreditCards()
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
			
			resource.getCreditCards().removeIf(c -> token.equals(c.getToken()));
			
			resource.addCreditCard(creditCard);
			
			updateAccountProfile(id, resource);
			
		} else {
			LOGGER.error(creditCardResult.getMessage());
			throw new ServiceException(creditCardResult.getMessage());
		}
	}
	
	public CreditCardDTO updateCreditCard(Id id, String token, MultivaluedMap<String,String> parameters) {
		
		CreditCardDTO creditCard = getCreditCard(id, token);
		
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
	
	public void removeCreditCard(Id id, String token) {
		AccountProfile resource = findAccountProfile(id);
		
		com.braintreegateway.CreditCard creditCard = gateway.creditCard().find(token);
		
		Result<com.braintreegateway.CreditCard> creditCardResult = gateway.creditCard().delete(token);
		
		if (creditCardResult.isSuccess()) {
			gateway.address().delete(creditCard.getCustomerId(), creditCard.getBillingAddress().getId());
			
			resource.getCreditCards().removeIf(c -> token.equals(c.getToken()));
			
			updateAccountProfile(id, resource);
		} else {
			LOGGER.error(creditCardResult.getMessage());
			throw new ServiceException(creditCardResult.getMessage());
		}
	}
}