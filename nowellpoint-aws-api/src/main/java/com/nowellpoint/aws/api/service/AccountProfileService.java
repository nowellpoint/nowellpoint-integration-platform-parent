package com.nowellpoint.aws.api.service;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import javax.enterprise.event.Observes;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

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
import com.nowellpoint.aws.api.model.Photos;
import com.nowellpoint.aws.api.model.SystemReference;
import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.tools.TokenParser;

public class AccountProfileService extends AbstractDocumentService<AccountProfileDTO, AccountProfile> {
	
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
		
		if (resource.getLastLoginDate() == null) {
			resource.setLastLoginDate(original.getLastLoginDate());
		}
		
		if (resource.getPhotos() == null) {
			resource.setPhotos(original.getPhotos());
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
				throw new WebApplicationException( String.format( "Account Profile for subject: %s does not exist or you do not have access to view", subject ), Status.NOT_FOUND );
			}

			resource = modelMapper.map( accountProfile, AccountProfileDTO.class );

			hset( resource.getId(), subject, resource );
			hset( subject, AccountProfileDTO.class.getName(), resource );
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
				} catch (NotFoundException ignore) {
					
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
		
		if (resource.getCreditCards() == null || resource.getCreditCards().size() == 0) {
			creditCard.setPrimary(Boolean.TRUE);
		} else {
			creditCard.setPrimary(Boolean.FALSE);
		}
		
		creditCard.setNumber(creditCardResult.getTarget().getMaskedNumber());
		creditCard.setToken(creditCardResult.getTarget().getToken());
		creditCard.setImageUrl(creditCardResult.getTarget().getImageUrl());
		creditCard.setLastFour(creditCardResult.getTarget().getLast4());
		creditCard.setCardType(creditCardResult.getTarget().getCardType());
		creditCard.setAddedOn(Date.from(Instant.now()));
		
		creditCard.getBillingAddress().setCountry(addressResult.getTarget().getCountryName());
		
		resource.addCreditCard(creditCard);
		
		updateAccountProfile(resource);
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
		
		AddressRequest addressRequest = new AddressRequest()
				.countryCodeAlpha2(creditCard.getBillingAddress().getCountryCode())
				.firstName(creditCard.getBillingContact().getFirstName())
				.lastName(creditCard.getBillingContact().getLastName())
				.locality(creditCard.getBillingAddress().getCity())
				.region(creditCard.getBillingAddress().getState())
				.postalCode(creditCard.getBillingAddress().getPostalCode())
				.streetAddress(creditCard.getBillingAddress().getStreet());
		
		Result<Address> addressResult = gateway.address().update(creditCardResult.getTarget().getCustomerId(), creditCardResult.getTarget().getBillingAddress().getId(), addressRequest);
		
		creditCard.setUpdatedOn(Date.from(Instant.now()));
		
		creditCard.getBillingAddress().setCountry(addressResult.getTarget().getCountryName());
		
		resource.getCreditCards().removeIf(c -> token.equals(c.getToken()));
		
		resource.addCreditCard(creditCard);
		
		updateAccountProfile(resource);
	}
	
	public void removeCreditCard(String subject, String id, String token) {
		AccountProfileDTO resource = hget( AccountProfileDTO.class, id, subject );
		resource.setSubject(subject);
		
		com.braintreegateway.CreditCard creditCard = gateway.creditCard().find(token);
		
		gateway.creditCard().delete(token);
		
		gateway.address().delete(creditCard.getCustomerId(), creditCard.getBillingAddress().getId());
		
		resource.getCreditCards().removeIf(c -> token.equals(c.getToken()));
		
		updateAccountProfile(resource);
	}
}