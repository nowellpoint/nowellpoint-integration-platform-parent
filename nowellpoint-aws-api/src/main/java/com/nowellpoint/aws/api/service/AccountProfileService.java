package com.nowellpoint.aws.api.service;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

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
import com.braintreegateway.Address;
import com.braintreegateway.AddressRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.exceptions.NotFoundException;
import com.nowellpoint.aws.api.dto.AccountProfileDTO;
import com.nowellpoint.aws.api.dto.idp.Token;
import com.nowellpoint.aws.api.model.AccountProfile;
import com.nowellpoint.aws.api.model.CreditCard;
import com.nowellpoint.aws.api.model.IsoCountry;
import com.nowellpoint.aws.api.model.Photos;
import com.nowellpoint.aws.api.model.SystemReference;
import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.tools.TokenParser;

public class AccountProfileService extends AbstractDocumentService<AccountProfileDTO, AccountProfile> {
	
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
		super(AccountProfileDTO.class, AccountProfile.class);
	}
	
	/**
	 * 
	 * @param token
	 */
	
	public void loggedInEvent(@Observes Token token) {
		String subject = TokenParser.parseToken(System.getProperty(Properties.STORMPATH_API_KEY_SECRET), token.getAccessToken());
		
		AccountProfileDTO resource = findAccountProfileBySubject(subject);
		resource.setLastLoginDate(Date.from(Instant.now()));
		resource.setSubject(subject);
		
		updateAccountProfile(resource);
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return the created Identity resource
	 */
	
	public AccountProfileDTO createAccountProfile(AccountProfileDTO resource) {
		resource.setHref(resource.getSubject());
		resource.setUsername(resource.getEmail());
		resource.setName(resource.getFirstName() != null ? resource.getFirstName().concat(" ").concat(resource.getLastName()) : resource.getLastName());
		
		IsoCountry isoCountry = isoCountryService.lookupByIso2Code(resource.getAddress().getCountryCode(), "US");
		
		resource.getAddress().setCountry(isoCountry.getDescription());
		
		Photos photos = new Photos();
		photos.setProfilePicture("/images/person-generic.jpg");
		
		resource.setPhotos(photos);
		
		create(resource);
		
		hset( resource.getId(), resource.getSubject(), resource );
		hset( resource.getSubject(), AccountProfileDTO.class.getName(), resource );
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return the updated Identity resource
	 */
	
	public AccountProfileDTO updateAccountProfile(AccountProfileDTO resource) {
		AccountProfileDTO original = findAccountProfile( resource.getId(), resource.getSubject() );
		resource.setName(resource.getFirstName() != null ? resource.getFirstName().concat(" ").concat(resource.getLastName()) : resource.getLastName());
		resource.setCreatedById(original.getCreatedById());
		resource.setCreatedDate(original.getCreatedDate());
		resource.setHref(original.getHref());
		resource.setEmailEncodingKey(original.getEmailEncodingKey());
		resource.setIsActive(original.getIsActive());
		resource.setLocaleSidKey(original.getLocaleSidKey());
		resource.setTimeZoneSidKey(original.getTimeZoneSidKey());
		
		if (original.getAddress().getCountryCode() != resource.getAddress().getCountryCode()) {
			IsoCountry isoCountry = isoCountryService.lookupByIso2Code(resource.getAddress().getCountryCode(), "US");
			resource.getAddress().setCountry(isoCountry.getDescription());
		}
		
		if (resource.getLastLoginDate() == null) {
			resource.setLastLoginDate(original.getLastLoginDate());
		}
		
		if (resource.getPhotos() == null) {
			resource.setPhotos(original.getPhotos());
		}
		
		if (resource.getCreditCards() == null) {
			resource.setCreditCards(original.getCreditCards());
		}
		
		replace(resource);

		hset( resource.getId(), resource.getSubject(), resource );
		hset( resource.getSubject(), AccountProfileDTO.class.getName(), resource );
		
		return resource;
	}
	
	/**
	 * 
	 * @param id
	 * @return Identity resource for id
	 */
	
	public AccountProfileDTO findAccountProfile(String id, String subject) {
		AccountProfileDTO resource = hget( AccountProfileDTO.class, id, subject );
		
		if ( resource == null ) {
			resource = find(id);
			hset( resource.getId(), subject, resource );
			hset( subject, AccountProfileDTO.class.getName(), resource );
		}
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @return Identity resource for subject
	 */
	
	public AccountProfileDTO findAccountProfileBySubject(String subject) {
		AccountProfileDTO resource = hget( AccountProfileDTO.class, subject, AccountProfileDTO.class.getName() );

		if ( resource == null ) {		

			AccountProfile accountProfile = MongoDBDatastore.getDatabase()
					.getCollection( AccountProfile.class.getAnnotation(Document.class).collectionName() )
					.withDocumentClass( AccountProfile.class )
					.find( eq ( "href", subject ) )
					.first();
			
			if ( accountProfile == null ) {
				return null;
			}

			resource = modelMapper.map( accountProfile, AccountProfileDTO.class );

			hset( resource.getId(), subject, resource );
			hset( subject, AccountProfileDTO.class.getName(), resource );
		}
		
		return resource;		
	}
	
	public AccountProfileDTO findAccountProfileByUsername(String username) {
		Optional<AccountProfile> queryResult = Optional.ofNullable( MongoDBDatastore.getDatabase().getCollection( AccountProfile.class.getAnnotation(Document.class).collectionName() )
				.withDocumentClass( AccountProfile.class )
				.find( eq( "username", username ) )
				.first() );
		
		AccountProfileDTO resource = null;
		
		if (queryResult.isPresent()) {
			resource = modelMapper.map( queryResult.get(), AccountProfileDTO.class );
		}
		
		return resource;
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
	
	public CreditCard getCreditCard(String subject, String id, String token) {
		AccountProfileDTO resource = hget( AccountProfileDTO.class, id, subject );
		
		Optional<CreditCard> creditCard = resource.getCreditCards()
				.stream()
				.filter(c -> token.equals(c.getToken()))
				.findFirst();
		
		return creditCard.get();
		
	}
	
	public void addCreditCard(String subject, String id, CreditCard creditCard) {
		AccountProfileDTO resource = hget( AccountProfileDTO.class, id, subject );
		resource.setSubject(subject);
		
		CustomerRequest customerRequest = new CustomerRequest()
				.company(resource.getCompany())
				.email(resource.getEmail())
				.firstName(resource.getFirstName())
				.lastName(resource.getLastName())
				.phone(resource.getPhone());
		
		Result<Customer> customerResult = null;
		
		if (resource.getSystemReferences() != null) {
			
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
		
		Result<Address> addressResult = gateway.address().create(customerResult.getTarget().getId(), addressRequest);
		
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
			
			updateAccountProfile(resource);
			
		} else {
			LOGGER.error(creditCardResult.getMessage());
			throw new ServiceException(creditCardResult.getMessage());
		}
	}
	
	public void updateCreditCard(String subject, String id, String token, CreditCard creditCard) {
		AccountProfileDTO resource = hget( AccountProfileDTO.class, id, subject );
		resource.setSubject(subject);
		
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
			
			Result<Address> addressResult = gateway.address().update(creditCardResult.getTarget().getCustomerId(), creditCardResult.getTarget().getBillingAddress().getId(), addressRequest);
			
			if (creditCard.getPrimary()) {
				resource.getCreditCards().stream().filter(c -> ! c.getToken().equals(token)).forEach(c -> {
					if (c.getPrimary()) {
						c.setPrimary(Boolean.FALSE);
					}
				});			
			}
			
			CreditCard original = resource.getCreditCards()
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
			
			updateAccountProfile(resource);
			
		} else {
			LOGGER.error(creditCardResult.getMessage());
			throw new ServiceException(creditCardResult.getMessage());
		}
	}
	
	public CreditCard updateCreditCard(String subject, String id, String token, MultivaluedMap<String,String> parameters) {
		
		CreditCard creditCard = getCreditCard(subject, id, token);
		
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
		
		updateCreditCard(subject, id, token, creditCard);
		
		return creditCard;
	}
	
	public void removeCreditCard(String subject, String id, String token) {
		AccountProfileDTO resource = hget( AccountProfileDTO.class, id, subject );
		resource.setSubject(subject);
		
		com.braintreegateway.CreditCard creditCard = gateway.creditCard().find(token);
		
		Result<com.braintreegateway.CreditCard> creditCardResult = gateway.creditCard().delete(token);
		
		if (creditCardResult.isSuccess()) {
			gateway.address().delete(creditCard.getCustomerId(), creditCard.getBillingAddress().getId());
			
			resource.getCreditCards().removeIf(c -> token.equals(c.getToken()));
			
			updateAccountProfile(resource);
		} else {
			LOGGER.error(creditCardResult.getMessage());
			throw new ServiceException(creditCardResult.getMessage());
		}
	}
}