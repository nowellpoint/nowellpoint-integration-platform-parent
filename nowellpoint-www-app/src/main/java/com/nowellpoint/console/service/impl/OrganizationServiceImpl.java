package com.nowellpoint.console.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.types.ObjectId;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nowellpoint.client.sforce.Salesforce;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.model.CreateResult;
import com.nowellpoint.client.sforce.model.PushTopic;
import com.nowellpoint.client.sforce.model.PushTopicRequest;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.console.entity.OrganizationDAO;
import com.nowellpoint.console.exception.ServiceException;
import com.nowellpoint.console.model.Address;
import com.nowellpoint.console.model.AddressRequest;
import com.nowellpoint.console.model.Connection;
import com.nowellpoint.console.model.ContactRequest;
import com.nowellpoint.console.model.CreditCard;
import com.nowellpoint.console.model.CreditCardRequest;
import com.nowellpoint.console.model.Dashboard;
import com.nowellpoint.console.model.Limits;
import com.nowellpoint.console.model.MetadataComponent;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.OrganizationRequest;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.EventStreamListener;
import com.nowellpoint.console.model.EventStreamListenerRequest;
import com.nowellpoint.console.model.Subscription;
import com.nowellpoint.console.model.SubscriptionRequest;
import com.nowellpoint.console.model.Transaction;
import com.nowellpoint.console.model.UserInfo;
import com.nowellpoint.console.model.UserLicenses;
import com.nowellpoint.console.service.AbstractService;
import com.nowellpoint.console.service.OrganizationService;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.UserContext;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties;
import com.nowellpoint.util.SecretsManager;
import com.nowellpoint.util.SecureValue;
import com.nowellpoint.util.SecureValueException;

public class OrganizationServiceImpl extends AbstractService implements OrganizationService {
	
	private static final Logger LOGGER = Logger.getLogger(OrganizationServiceImpl.class.getName());
	private static final String S3_BUCKET = "streaming-event-listener-us-east-1-600862814314";
	
	private static BraintreeGateway gateway = new BraintreeGateway(
			Environment.parseEnvironment(SecretsManager.getBraintreeEnvironment()),
			SecretsManager.getBraintreeMerchantId(),
			SecretsManager.getBraintreePublicKey(),
			SecretsManager.getBraintreePrivateKey()
	);
	
	static {
		gateway.clientToken().generate();
	}
	
	private OrganizationDAO dao;
	
	public OrganizationServiceImpl() {
		dao = new OrganizationDAO(com.nowellpoint.console.entity.Organization.class, datastore);
	}
	
	@Override
	public Organization create(OrganizationRequest request) {
		
		Plan plan = ServiceClient.getInstance()
				.plan()
				.get(request.getPlanId());
		
		com.braintreegateway.CustomerRequest customerRequest = new com.braintreegateway.CustomerRequest()
				.company(request.getName())
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName());
		
		Result<com.braintreegateway.Customer> customerResult = gateway.customer().create(customerRequest);
		
		if ( ! customerResult.isSuccess() ) {
			throw new ServiceException(customerResult.getMessage());
		}
		
		Subscription subscription = Subscription.builder()
				.addedOn(getCurrentDateTime())
				.billingFrequency(plan.getBillingFrequency())
				.currencyIsoCode(plan.getPrice().getCurrencyIsoCode())
				.currencySymbol(plan.getPrice().getCurrencySymbol())
				.features(plan.getFeatures())
				.planCode(plan.getPlanCode())
				.planId(plan.getId())
				.planName(plan.getPlanName())
				.unitPrice(plan.getPrice().getUnitPrice())
				.updatedOn(getCurrentDateTime())
				.status(com.braintreegateway.Subscription.Status.ACTIVE.name())
				.build();
		
		Dashboard dashboard = Dashboard.builder()
				.build();
		
		Address address = Address.builder()
				.addedOn(getCurrentDateTime())
				.updatedOn(getCurrentDateTime())
				.build();
		
		Organization organization = Organization.builder()
				.address(address)
				.dashboard(dashboard)
				.domain(request.getDomain())
				.eventStreamListeners(plan.getEventStreamListeners())
				.name(request.getName())
				.number(customerResult.getTarget().getId())
				.subscription(subscription)
				.build();
		
		return create(organization);
	}
	
	@Override
	public Organization update(String id, String authorizationCode) {
		Token token = ServiceClient.getInstance()
        		.salesforce()
        		.getToken(authorizationCode);
		
		Organization instance = get(id);
		
		com.nowellpoint.client.sforce.model.Identity identity = ServiceClient.getInstance()
				.salesforce()
				.getIdentity(token);
		
		String ecryptedToken = null;
		
		try {
			ecryptedToken = SecureValue.encryptBase64(token.getRefreshToken());
		} catch (SecureValueException e) {
			LOGGER.severe(ExceptionUtils.getStackTrace(e));
		}
		
		Organization organization = Organization.builder()
				.from(instance)
				.connection(Connection.builder()
						.connectedAs(identity.getUsername())
						.connectedAt(getCurrentDateTime())
						.id(identity.getId())
						.instanceUrl(token.getInstanceUrl())
						.isConnected(Boolean.TRUE)
						.issuedAt(token.getIssuedAt())
						.refreshToken(ecryptedToken)
						.status(Connection.CONNECTED)
						.tokenType(token.getTokenType())
						.build())
				.domain(identity.getOrganizationId())
				.build();
		
		saveConfiguration(organization);
		
	//	event.fire(organization);
		
		return update(organization);
	}
	
	@Override
	public Organization refresh(String id) {
		
		Organization organization = syncOrganization(id);
		
		com.braintreegateway.CustomerRequest customerRequest = new com.braintreegateway.CustomerRequest()
				.company(organization.getName());
		
		Result<com.braintreegateway.Customer> customerResult = gateway.customer().update(organization.getNumber(), customerRequest);
		
		if ( ! customerResult.isSuccess() ) {
			throw new ServiceException(customerResult.getMessage());
		}
		
		return update(organization);
	}

	@Override
	public Organization get(String id) {
		com.nowellpoint.console.entity.Organization entity = getEntry(id);
		if (entity == null) {
			try {
				entity = dao.get(new ObjectId(id));
			} catch (IllegalArgumentException e) {
				throw new BadRequestException(String.format("Invalid Organization Id: %s", id));
			}
			
			if (Assert.isNull(entity)) {
				throw new NotFoundException(String.format("Organization Id: %s was not found",id));
			}
		}

		return Organization.of(entity);
	}
	
	@Override
	public Organization update(String id, EventStreamListenerRequest request) {
		Organization instance = get(id);
		
		Optional<EventStreamListener> listenerOptional = instance.getEventStreamListeners()
				.stream()
				.filter(l -> request.getSource().equals(l.getSource()))
				.findFirst();
		
		List<EventStreamListener> listeners = new ArrayList<EventStreamListener>(instance.getEventStreamListeners());
		
		if (listenerOptional.isPresent()) {
			
			Token token = ServiceClient.getInstance()
	        		.salesforce()
	        		.refreshToken(instance.getConnection().getRefreshToken());

			EventStreamListener listener = EventStreamListener.builder()
					.from(listenerOptional.get())
					.active(request.isActive())
					.apiVersion(instance.getConnection().getApiVersion())
					.notifyForOperationCreate(request.getNotifyForOperationCreate())
					.notifyForOperationDelete(request.getNotifyForOperationDelete())
					.notifyForOperationUndelete(request.getNotifyForOperationUndelete())
					.notifyForOperationUpdate(request.getNotifyForOperationUpdate())
					.startedOn(request.isActive() && ! listenerOptional.get().getActive() ? new Date() : listenerOptional.get().getStartedOn())
					.stoppedOn(! request.isActive() && listenerOptional.get().getActive() ? new Date() : listenerOptional.get().getStoppedOn())
					.build();
			
			String topicId = savePushTopic(token, listener);

			listeners.removeIf(l -> listenerOptional.get().getSource().equals(l.getSource()));
			
			UserInfo user = UserInfo.of(UserContext.get());
			
			Date now = getCurrentDateTime();
			
			Salesforce client = SalesforceClientBuilder.defaultClient(token);

			PushTopic pushTopic = client.getPushTopic(topicId);

			if (pushTopic.getCreatedDate().equals(pushTopic.getLastModifiedDate())) {
				
				listeners.add(EventStreamListener.builder()
						.from(listener)
						.createdBy(user)
						.createdOn(now)
						.lastUpdatedBy(user)
						.lastUpdatedOn(now)
						.topicId(pushTopic.getId())
						.build());
				
			} else {
				
				listeners.add(EventStreamListener.builder()
						.from(listener)
						.lastUpdatedBy(user)
						.lastUpdatedOn(now)
						.build());
			}
		}
		
		Organization organization = Organization.builder()
				.from(instance)
				.eventStreamListeners(listeners)
				.build();
		
		saveConfiguration(organization);
		
		return update(organization);
	}
	
	@Override
	public Organization update(String id, SubscriptionRequest request) {
		Organization instance = get(id);
		
		Organization organization = Organization.builder()
				.from(instance)
				.subscription(Subscription.builder()
						.from(instance.getSubscription())
						.billingPeriodEndDate(request.getBillingPeriodEndDate())
						.billingPeriodStartDate(request.getBillingPeriodStartDate())
						.nextBillingDate(request.getNextBillingDate())
						.transactions(request.getTransactions())
						.build())
				.build();
		
		return update(organization);
	}
	
	@Override
	public Organization update(String id, CreditCardRequest request) {
		
		Organization instance = get(id);
		
		com.braintreegateway.CreditCardRequest creditCardRequest = new com.braintreegateway.CreditCardRequest()
				.billingAddressId(instance.getSubscription().getBillingAddress().getId())
				.cardholderName(request.getCardholderName())
				.customerId(instance.getNumber())
				.cvv(Assert.isEmpty(request.getCvv()) ? null : request.getCvv())
				.expirationMonth(request.getExpirationMonth())
				.expirationYear(request.getExpirationYear())
				.number(Assert.isEmpty(request.getNumber()) ? null : request.getNumber());
		
		Result<com.braintreegateway.CreditCard> creditCardResult = gateway.creditCard().update(instance.getSubscription().getCreditCard().getToken(), creditCardRequest);
		
		if ( ! creditCardResult.isSuccess() ) {
			throw new ServiceException(creditCardResult.getMessage());
		}
		
		CreditCard creditCard = CreditCard.builder()
				.from(instance.getSubscription().getCreditCard())
				.cardholderName(creditCardResult.getTarget().getCardholderName())
				.cardType(creditCardResult.getTarget().getCardType())
				.expirationMonth(request.getExpirationMonth())
				.expirationYear(request.getExpirationYear())
				.imageUrl(creditCardResult.getTarget().getImageUrl())
				.lastFour(creditCardResult.getTarget().getLast4())
				.updatedOn(getCurrentDateTime())
				.build();
		
		Subscription subscription = Subscription.builder()
				.from(instance.getSubscription())
				.creditCard(creditCard)
				.build();
		
		Organization organization = Organization.builder()
				.from(instance)
				.subscription(subscription)
				.build();
		
		return update(organization);
	}
	
	@Override
	public Organization setPlan(String id, Plan plan) {
		
		Organization instance = get(id);
		
		com.braintreegateway.SubscriptionRequest subscriptionRequest = new com.braintreegateway.SubscriptionRequest()
				.paymentMethodToken(instance.getSubscription().getCreditCard().getToken())
				.planId(plan.getPlanCode())
				.price(new BigDecimal(plan.getPrice().getUnitPrice()));
		
		Result<com.braintreegateway.Subscription> subscriptionResult = null;
		
		Optional<String> number = Optional.of(instance)
				.map(Organization::getSubscription)
				.map(Subscription::getNumber);
		
		if (number.isPresent()) {
			subscriptionResult = gateway.subscription().update(instance.getSubscription().getNumber(), subscriptionRequest);
		} else {
			subscriptionResult = gateway.subscription().create(subscriptionRequest);
		}
		
		if ( ! subscriptionResult.isSuccess() ) {
			throw new ServiceException(subscriptionResult.getMessage());
		}
		
		Set<Transaction> transactions = new HashSet<>();
		
		subscriptionResult.getTarget().getTransactions().stream().forEach(t -> {
			transactions.add(Transaction.of(t));
		});
		
		Subscription subscription = Subscription.builder()
				.from(instance.getSubscription())
				.billingFrequency(plan.getBillingFrequency())
				.currencyIsoCode(plan.getPrice().getCurrencyIsoCode())
				.currencySymbol(plan.getPrice().getCurrencySymbol())
				.billingPeriodEndDate(subscriptionResult.getTarget().getBillingPeriodEndDate().getTime())
				.billingPeriodStartDate(subscriptionResult.getTarget().getBillingPeriodStartDate().getTime())
				.features(plan.getFeatures())
				.nextBillingDate(subscriptionResult.getTarget().getNextBillingDate().getTime())
				.planCode(plan.getPlanCode())
				.planId(plan.getId())
				.planName(plan.getPlanName())
				.status(subscriptionResult.getTarget().getStatus().name())
				.transactions(transactions)
				.unitPrice(plan.getPrice().getUnitPrice())
				.updatedOn(getCurrentDateTime())
				.build();
		
		Organization organization = Organization.builder()
				.from(instance)
				.eventStreamListeners(plan.getEventStreamListeners())
				.subscription(subscription)
				.build();
		
		return update(organization);
	}
	
	@Override
	public Organization setPlan(String id, Plan plan, CreditCardRequest request) {
		
		Organization instance = get(id);
		
		com.braintreegateway.CreditCardRequest creditCardRequest = new com.braintreegateway.CreditCardRequest()
				.billingAddressId(instance.getSubscription().getBillingAddress().getId())
				.cardholderName(request.getCardholderName())
				.customerId(instance.getNumber())
				.cvv(Assert.isEmpty(request.getCvv()) ? null : request.getCvv())
				.expirationMonth(request.getExpirationMonth())
				.expirationYear(request.getExpirationYear())
				.number(Assert.isEmpty(request.getNumber()) ? null : request.getNumber());
		
		Result<com.braintreegateway.CreditCard> creditCardResult = null;
		
		Optional<String> token = Optional.of(instance)
				.map(Organization::getSubscription)
				.map(Subscription::getCreditCard)
				.map(CreditCard::getToken);
		
		if (token.isPresent()) {
			creditCardResult = gateway.creditCard().update(instance.getSubscription().getCreditCard().getToken(), creditCardRequest);
		} else {
			creditCardResult = gateway.creditCard().create(creditCardRequest);
		}
		
		if (! creditCardResult.isSuccess()) {
			throw new ServiceException(creditCardResult.getMessage());
		}
		
		com.braintreegateway.SubscriptionRequest subscriptionRequest = new com.braintreegateway.SubscriptionRequest()
				.paymentMethodToken(creditCardResult.getTarget().getToken())
				.planId(plan.getPlanCode())
				.price(new BigDecimal(plan.getPrice().getUnitPrice()));
		
		Result<com.braintreegateway.Subscription> subscriptionResult = null;
		
		Optional<String> number = Optional.of(instance)
				.map(Organization::getSubscription)
				.map(Subscription::getNumber);
		
		if (number.isPresent()) {
			subscriptionResult = gateway.subscription().update(instance.getSubscription().getNumber(), subscriptionRequest);
		} else {
			subscriptionResult = gateway.subscription().create(subscriptionRequest);
		}
		
		if ( ! subscriptionResult.isSuccess() ) {
			throw new ServiceException(subscriptionResult.getMessage());
		}
		
		Transaction transaction = Transaction.of(subscriptionResult.getTransaction());
		
		Subscription subscription = Subscription.builder()
				.from(instance.getSubscription())
				.addTransaction(transaction)
				.billingFrequency(plan.getBillingFrequency())
				.creditCard(transaction.getCreditCard())
				.currencyIsoCode(plan.getPrice().getCurrencyIsoCode())
				.currencySymbol(plan.getPrice().getCurrencySymbol())
				.billingPeriodEndDate(subscriptionResult.getTarget().getBillingPeriodEndDate().getTime())
				.billingPeriodStartDate(subscriptionResult.getTarget().getBillingPeriodStartDate().getTime())
				.features(plan.getFeatures())
				.planCode(plan.getPlanCode())
				.planId(plan.getId())
				.planName(plan.getPlanName())
				.status(subscriptionResult.getTarget().getStatus().name())
				.unitPrice(plan.getPrice().getUnitPrice())
				.updatedOn(getCurrentDateTime())
				.build();
		
		Organization organization = Organization.builder()
				.from(instance)
				.eventStreamListeners(plan.getEventStreamListeners())
				.subscription(subscription)
				.build();
		
		return update(organization);
	}
	
	@Override
	public Organization update(String id, AddressRequest request) {
		
		Organization instance = get(id);
		
		com.braintreegateway.AddressRequest addressRequest = new com.braintreegateway.AddressRequest()
				.countryCodeAlpha2(request.getCountryCode())
				.locality(request.getState())
				.postalCode(request.getPostalCode())
				.region(request.getState())
				.streetAddress(request.getStreet());
		
		Result<com.braintreegateway.Address> addressResult = null;
		
		Optional<String> addressId = Optional.of(instance)
				.map(Organization::getSubscription)
				.map(Subscription::getBillingAddress)
				.map(Address::getId);
		
		if (addressId.isPresent()) {
			addressResult = gateway.address().update(instance.getNumber(), addressId.get(), addressRequest);
		} else {
			addressResult = gateway.address().create(instance.getNumber(), addressRequest);
		}
		
		if ( ! addressResult.isSuccess() ) {
			throw new ServiceException(addressResult.getMessage());
		}
		
		Address billingAddress = Address.builder()
				.from(instance.getSubscription().getBillingAddress())
				.city(request.getCity())
				.countryCode(request.getCountryCode())
				.id(addressResult.getTarget().getId())
				.postalCode(request.getPostalCode())
				.state(request.getState())
				.street(request.getStreet())
				.updatedOn(getCurrentDateTime())
				.build();
		
		Subscription subscription = Subscription.builder()
				.from(instance.getSubscription())
				.billingAddress(billingAddress)
				.updatedOn(getCurrentDateTime())
				.build();

		Organization organization = Organization.builder()
				.from(instance)
				.subscription(subscription)
				.build();
		
		return update(organization);
	}
	
	@Override
	public Organization update(String id, ContactRequest request) {
		
		Organization instance = get(id);
		
		com.braintreegateway.CustomerRequest customerRequest = new com.braintreegateway.CustomerRequest()
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.phone(request.getPhone());
		
		Result<com.braintreegateway.Customer> customerResult = gateway.customer().update(instance.getNumber(), customerRequest);
		
		if ( ! customerResult.isSuccess() ) {
			throw new ServiceException(customerResult.getMessage());
		}
		
		Subscription subscription = Subscription.builder()
				.from(instance.getSubscription())
				.build();
		
		Organization organization = Organization.builder()
				.from(instance)
				.subscription(subscription)
				.build();
		
		return update(organization);
	}
	
	@Override
	public void delete(String id) {
		Organization organization = get(id);
		deleteCustomer(organization.getNumber());
		delete(organization);
	}
	
	@Override
	public List<Organization> refreshAll() {
		List<Organization> organizations = dao.getOrganizations()
				.stream()
				.filter(organization -> organization.getConnection().getIsConnected())
				.map(organization -> {
					refresh(organization.getId().toString());
					return Organization.of(organization);
				})
				.collect(Collectors.toList());
		
		return organizations;
	}
	
	@Override
	public byte[] createInvoice(String id, String invoiceNumber) throws IOException {
		
		Organization organization = get(id);
		
		Optional<Transaction> optional = organization.getSubscription()
				.getTransactions()
				.stream()
				.filter(t -> t.getId().equals(invoiceNumber))
				.findFirst();
		
		if (optional.isPresent()) {
			
			Transaction transaction = optional.get();
			
			Plan plan = ServiceClient.getInstance()
					.plan()
					.getByCode(transaction.getPlan());
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
			Document document = new Document();
			
			try {
				
				PdfWriter.getInstance(document, baos);
				
				document.open();
				
				document.setMargins(75, 75, 75, 10);
//				document.addTitle(getLabel(InvoiceLabels.INVOICE_TITLE));
//				document.addAuthor(getLabel(InvoiceLabels.INVOICE_AUTHOR));
//				document.addSubject(String.format(getLabel(InvoiceLabels.INVOICE_SUBJECT), invoice.getPayee().getCompanyName()));
				document.addCreator(OrganizationService.class.getName());
				 
				document.add(getHeader(organization, transaction));
				document.add(new Chunk("Invoice", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));
				document.add(getPayer(organization, transaction));
				document.add(Chunk.NEWLINE);
				document.add(getPlan(plan, transaction));
				document.add(getPaymentMethod(transaction));
				
				document.close();  
				
			} catch (DocumentException e) {
				throw new IOException(e);
			}
			
			return baos.toByteArray();
		
		}
		
		return null;
	}
	
	private Organization syncOrganization(String id) {
		
		Organization instance = get(id);
		
		Token token = ServiceClient.getInstance()
				.salesforce()
				.refreshToken(instance.getConnection().getRefreshToken());
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		
		FutureTask<com.nowellpoint.client.sforce.model.Organization> getOrganizationTask = new FutureTask<com.nowellpoint.client.sforce.model.Organization>(
				new Callable<com.nowellpoint.client.sforce.model.Organization>() {
					@Override
					public com.nowellpoint.client.sforce.model.Organization call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getOrganization(token);
				   }
				}
		);
		
		executor.execute(getOrganizationTask);
		
		FutureTask<com.nowellpoint.client.sforce.model.Identity> getIdentityTask = new FutureTask<com.nowellpoint.client.sforce.model.Identity>(
				new Callable<com.nowellpoint.client.sforce.model.Identity>() {
					@Override
					public com.nowellpoint.client.sforce.model.Identity call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getIdentity(token);
				   }
				}
		);
		
		executor.execute(getIdentityTask);
		
		FutureTask<Set<com.nowellpoint.client.sforce.model.sobject.SObject>> getCustomObjectsTask = new FutureTask<Set<com.nowellpoint.client.sforce.model.sobject.SObject>>(
				new Callable<Set<com.nowellpoint.client.sforce.model.sobject.SObject>>() {
					@Override
					public Set<com.nowellpoint.client.sforce.model.sobject.SObject> call() {
						return ServiceClient.getInstance()
								.salesforce()
				 				.getCustomObjects(token);
				   }
				}
		);
		
		executor.execute(getCustomObjectsTask);
		
		FutureTask<com.nowellpoint.client.sforce.model.UserLicense[]> getUserLicensesTask = new FutureTask<com.nowellpoint.client.sforce.model.UserLicense[]>(
				new Callable<com.nowellpoint.client.sforce.model.UserLicense[]>() {
					@Override
					public com.nowellpoint.client.sforce.model.UserLicense[] call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getUserLicenses(token);
				   }
				}
		);
		
		executor.execute(getUserLicensesTask);
		
		FutureTask<Set<com.nowellpoint.client.sforce.model.ApexClass>> getApexClassesTask = new FutureTask<Set<com.nowellpoint.client.sforce.model.ApexClass>>(
				new Callable<Set<com.nowellpoint.client.sforce.model.ApexClass>>() {
					@Override
					public Set<com.nowellpoint.client.sforce.model.ApexClass> call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getApexClasses(token);
				   }
				}
		);
		
		executor.execute(getApexClassesTask);
		
		FutureTask<Set<com.nowellpoint.client.sforce.model.ApexTrigger>> getApexTriggersTask = new FutureTask<Set<com.nowellpoint.client.sforce.model.ApexTrigger>>(
				new Callable<Set<com.nowellpoint.client.sforce.model.ApexTrigger>>() {
					@Override
					public Set<com.nowellpoint.client.sforce.model.ApexTrigger> call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getApexTriggers(token);
				   }
				}
		);
		
		executor.execute(getApexTriggersTask);
		
		FutureTask<Set<com.nowellpoint.client.sforce.model.RecordType>> getRecordTypesTask = new FutureTask<Set<com.nowellpoint.client.sforce.model.RecordType>>(
				new Callable<Set<com.nowellpoint.client.sforce.model.RecordType>>() {
					@Override
					public Set<com.nowellpoint.client.sforce.model.RecordType> call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getRecordTypes(token);
				   }
				}
		);
		
		executor.execute(getRecordTypesTask);
		
		FutureTask<Set<com.nowellpoint.client.sforce.model.UserRole>> getUserRolesTask = new FutureTask<Set<com.nowellpoint.client.sforce.model.UserRole>>(
				new Callable<Set<com.nowellpoint.client.sforce.model.UserRole>>() {
					@Override
					public Set<com.nowellpoint.client.sforce.model.UserRole> call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getUserRoles(token);
				   }
				}
		);
		
		executor.execute(getUserRolesTask);
		
		FutureTask<Set<com.nowellpoint.client.sforce.model.Profile>> getProfilesTask = new FutureTask<Set<com.nowellpoint.client.sforce.model.Profile>>(
				new Callable<Set<com.nowellpoint.client.sforce.model.Profile>>() {
					@Override
					public Set<com.nowellpoint.client.sforce.model.Profile> call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getProfiles(token);
				   }
				}
		);
		
		executor.execute(getProfilesTask);
		
		FutureTask<com.nowellpoint.client.sforce.model.Limits> getLimitsTask = new FutureTask<com.nowellpoint.client.sforce.model.Limits>(
				new Callable<com.nowellpoint.client.sforce.model.Limits>() {
					@Override
					public com.nowellpoint.client.sforce.model.Limits call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getLimits(token);
				   }
				}
		);
		
		executor.execute(getLimitsTask);
		
		try {
			
			Double apexClasses = Double.valueOf(getApexClassesTask.get().size());
			Double apexTriggers = Double.valueOf(getApexTriggersTask.get().size());
			Double customObjects = Double.valueOf(getCustomObjectsTask.get().size());
			Double profiles = Double.valueOf(getProfilesTask.get().size());
			Double recordTypes = Double.valueOf(getRecordTypesTask.get().size());
			Double userRoles = Double.valueOf(getUserRolesTask.get().size());
			
			Organization organization = Organization.builder()
					.from(instance)
					.address(Address.builder()
							.from(instance.getAddress())
							.city(getOrganizationTask.get().getAddress().getCity())
							.countryCode(getOrganizationTask.get().getAddress().getCountryCode())
							.latitude(getOrganizationTask.get().getAddress().getLatitude())
							.longitude(getOrganizationTask.get().getAddress().getLongitude())
							.postalCode(getOrganizationTask.get().getAddress().getPostalCode())
							.state(getOrganizationTask.get().getAddress().getState())
							.stateCode(getOrganizationTask.get().getAddress().getStateCode())
							.street(getOrganizationTask.get().getAddress().getStreet())
							.updatedOn(getCurrentDateTime())
							.build())
					.connection(Connection.builder()
							.from(instance.getConnection())
							.connectedAt(getCurrentDateTime())
							.instanceUrl(token.getInstanceUrl())
							.issuedAt(token.getIssuedAt())
							.build())
					.dashboard(Dashboard.builder()
							.from(instance.getDashboard())
							.apexClass(MetadataComponent.builder()
									.value(apexClasses)
									.delta(apexClasses - instance.getDashboard().getApexClass().getValue())
									.unit(MetadataComponent.QUANTITY)
									.build())
							.apexTrigger(MetadataComponent.builder()
									.value(apexTriggers)
									.delta(apexTriggers - instance.getDashboard().getApexTrigger().getValue())
									.unit(MetadataComponent.QUANTITY)
									.build())
							.customObject(MetadataComponent.builder()
									.value(customObjects)
									.delta(customObjects - instance.getDashboard().getCustomObject().getValue())
									.unit(MetadataComponent.QUANTITY)
									.build())
							.lastRefreshedOn(getCurrentDateTime())
							.limits(Limits.of(getLimitsTask.get()))
							.profile(MetadataComponent.builder()
									.value(profiles)
									.delta(profiles - instance.getDashboard().getProfile().getValue())
									.unit(MetadataComponent.QUANTITY)
									.build())
							.recordType(MetadataComponent.builder()
									.value(recordTypes)
									.delta(recordTypes - instance.getDashboard().getRecordType().getValue())
									.unit(MetadataComponent.QUANTITY)
									.build())
							.userLicenses(UserLicenses.of(getUserLicensesTask.get()))
							.userRole(MetadataComponent.builder()
									.value(userRoles)
									.delta(userRoles - instance.getDashboard().getUserRole().getValue())
									.unit(MetadataComponent.QUANTITY)
									.build())
							.build())
					.name(getOrganizationTask.get().getName())
					.organizationType(getOrganizationTask.get().getOrganizationType())
					.build();
			
			return organization;
			
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.severe("Unable to sync organization: " + ExceptionUtils.getStackTrace(e));
			return instance;
		}
	}
	
	private String savePushTopic(Token token, EventStreamListener listener) {
		PushTopicRequest pushTopicRequest = PushTopicRequest.builder()
				.isActive(listener.getActive())
				.apiVersion(listener.getApiVersion())
				.description(listener.getDescription())
				.name(listener.getName())
				.notifyForOperationCreate(listener.getNotifyForOperationCreate())
				.notifyForOperationDelete(listener.getNotifyForOperationDelete())
				.notifyForOperationUndelete(listener.getNotifyForOperationUndelete())
				.notifyForOperationUpdate(listener.getNotifyForOperationUpdate())
				.notifyForFields("All")
				.query(listener.getQuery())
				.build();
		
		Salesforce client = SalesforceClientBuilder.defaultClient(token);
		
		String topicId = listener.getTopicId();
		
		if (Assert.isNullOrEmpty(topicId)) {
			CreateResult createResult = client.createPushTopic(pushTopicRequest);
			topicId = createResult.getId();
		} else {
			client.updatePushTopic(listener.getTopicId(), pushTopicRequest);
		}
		
		return topicId;
	}
	
	private Organization create(Organization organization) {
		com.nowellpoint.console.entity.Organization entity = modelMapper.map(organization, com.nowellpoint.console.entity.Organization.class);
		entity.setCreatedBy(new com.nowellpoint.console.entity.Identity(UserContext.get() != null ? UserContext.get().getId() : getSystemAdmin().getId().toString()));
		entity.setLastUpdatedBy(entity.getCreatedBy());
		dao.save(entity);
		entity = dao.get(entity.getId());
		putEntry(entity.getId().toString(), entity);
		return Organization.of(entity);
	}
	
	private Organization update(Organization organization) {
		com.nowellpoint.console.entity.Organization entity = modelMapper.map(organization, com.nowellpoint.console.entity.Organization.class);
		entity.setLastUpdatedOn(getCurrentDateTime());
		entity.setLastUpdatedBy(new com.nowellpoint.console.entity.Identity(UserContext.get() != null ? UserContext.get().getId() : getSystemAdmin().getId().toString()));
		dao.save(entity);
		entity = dao.get(entity.getId());
		putEntry(entity.getId().toString(), entity);
		return Organization.of(entity);
	}
	
	private void saveConfiguration(Organization organization) {
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		JsonArrayBuilder builder = Json.createArrayBuilder();
		
		organization.getEventStreamListeners().forEach(l -> {
			builder.add(Json.createObjectBuilder()
					.add("channel", l.getChannel())
					.add("active", l.getActive())
					.add("source", l.getSource())
					.add("topicId", l.getTopicId() != null ? Json.createValue(l.getTopicId()) : JsonValue.NULL)
					.build());
		});
		
		JsonObject json = Json.createObjectBuilder()
			     .add("organizationId", organization.getId())
			     .add("apiVersion", organization.getConnection().getApiVersion())
			     .add("refreshToken", organization.getConnection().getRefreshToken())
			     .add("topics", builder.build())
			     .build();
		
		byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
		
		InputStream input = new ByteArrayInputStream(bytes);
		
		ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        metadata.setContentLength(bytes.length);
        
        String folder = "configuration/"
				.concat(System.getProperty(Properties.STREAMING_EVENT_LISTENER_QUEUE))
				.concat("/")
				.concat(organization.getId());
		
		PutObjectRequest request = new PutObjectRequest(S3_BUCKET, folder, input, metadata);
        
        s3client.putObject(request);
	}
	
	private PdfPTable getHeader(Organization organization, Transaction transaction) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setSpacingAfter(36f);
		table.addCell(getCell("NOWELLPOINT, LLC", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK)));
		table.addCell(getCell("Account Number:  " + String.format("%s", organization.getNumber()), PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell("129 S. Bloodworth Street", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell("Invoice Number:  " + String.format("%s", transaction.getId().toUpperCase()), PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell("Raleigh, NC 27601", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell("Invoice Date:  " + String.format("%s", sdf.format(transaction.getCreatedOn())), PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell("United States", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(" ", PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(" ", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(" ", PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell("Tax ID: 47-5575435", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(" ", PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		return table;
	}
	
	private PdfPTable getPayer(Organization organization, Transaction transaction) {
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		table.setSpacingBefore(18f);
		table.setSpacingAfter(36f);
		table.addCell(getCell(organization.getDomain().toUpperCase(), PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(transaction.getName(), PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(transaction.getBillingAddress().getStreet(), PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(transaction.getBillingAddress().getCity() + ", " + transaction.getBillingAddress().getState() + " " + transaction.getBillingAddress().getPostalCode(), PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(transaction.getBillingAddress().getCountry(), PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		return table;
	}
	
	private PdfPTable getPlan(Plan plan, Transaction transaction) {	
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, YYYY");
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100);
		table.setSpacingAfter(36f);
		table.addCell(getHeaderCell("Plan", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getHeaderCell("Billing Period", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getHeaderCell("Price", PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getPlanCell(plan.getPlanName(), PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getPlanCell(sdf.format(transaction.getBillingPeriodStartDate().getTime()) + " - " + sdf.format(transaction.getBillingPeriodEndDate().getTime()), PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getPlanCell(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(transaction.getAmount()), PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		return table;
	}
	
	private Chunk getPaymentMethod(Transaction transaction) {	
		Chunk chunk = new Chunk("Payment Method: " + transaction.getCreditCard().getCardType() + " " + transaction.getCreditCard().getLastFour(), FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
		return chunk;
	}
	
	private PdfPCell getCell(String text, int alignment, Font font) {
	    PdfPCell cell = new PdfPCell(new Phrase(text, font));
	    cell.setPadding(0);
	    cell.setPaddingBottom(2f);
	    cell.setHorizontalAlignment(alignment);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    return cell;
	}
	
	private PdfPCell getHeaderCell(String text, int alignment, Font font) {
	    PdfPCell cell = new PdfPCell(new Phrase(text, font));
	    cell.setPadding(2f);
	    cell.setPaddingLeft(5f);
	    cell.setPaddingRight(5f);
	    cell.setPaddingBottom(13f);
	    cell.setPaddingTop(13f);
	    cell.setHorizontalAlignment(alignment);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    return cell;
	}
	
	private PdfPCell getPlanCell(String text, int alignment, Font font) {
	    PdfPCell cell = new PdfPCell(new Phrase(text, font));
	    cell.setPadding(2f);
	    cell.setPaddingBottom(5f);
	    cell.setPaddingLeft(5f);
	    cell.setPaddingRight(5f);
	    cell.setHorizontalAlignment(alignment);
	    cell.setBorder(PdfPCell.BOTTOM);
	    return cell;
	}
	
	private void deleteCustomer(String number) {
		gateway.customer().delete(number);
	}
	
	private void delete(Organization organization) {
		com.nowellpoint.console.entity.Organization entity = modelMapper.map(organization, com.nowellpoint.console.entity.Organization.class);
		dao.delete(entity);
	}
}