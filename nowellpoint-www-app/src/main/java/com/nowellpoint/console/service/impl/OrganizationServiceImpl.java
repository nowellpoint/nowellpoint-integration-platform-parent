package com.nowellpoint.console.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.bson.types.ObjectId;

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
import com.nowellpoint.console.model.EventListenerRequest;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.OrganizationRequest;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.Subscription;
import com.nowellpoint.console.model.SubscriptionRequest;
import com.nowellpoint.console.model.Transaction;
import com.nowellpoint.console.service.AbstractService;
import com.nowellpoint.console.service.OrganizationService;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.SecretsManager;
import com.nowellpoint.console.util.UserContext;
import com.nowellpoint.util.Assert;

public class OrganizationServiceImpl extends AbstractService implements OrganizationService {
	
	private static final Logger logger = Logger.getLogger(OrganizationServiceImpl.class.getName());
	
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

//		Calendar today = Calendar.getInstance();
//		today.set(Calendar.HOUR_OF_DAY, 2);
//		today.set(Calendar.MINUTE, 0);
//		today.set(Calendar.SECOND, 0);
//		
//		Timer timer = new Timer();
//		timer.schedule(new TimerTask() {
//			public void run() {
//				List<com.nowellpoint.console.entity.Organization> organizations = dao.createQuery()
//						.field("connection")
//						.notEqual(null)
//						.asList();
//				
//				organizations.stream().forEach(instance -> {
//					System.out.println(new java.util.Date() + " " + instance.getName());
//					
//					//JsonNode tokenNode = refreshToken(String.valueOf(Base64.getDecoder().decode(instance.getConnection().getRefreshToken())));
//					
//					//System.out.println(tokenNode.toString());
//					
//					
//				});
//			}
//		}, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
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
		
		Organization organization = Organization.builder()
				.subscription(subscription)
				.dashboard(dashboard)
				.domain(request.getDomain())
				.name(request.getName())
				.number(customerResult.getTarget().getId())
				.build();
		
		return create(organization);
	}
	
	@Override
	public Organization update(String id, String authorizationCode) {
		Token token = ServiceClient.getInstance()
        		.salesforce()
        		.getToken(authorizationCode);
		
		return update(id, token);
	}
	
	@Override
	public Organization update(String id, Token token) {
		
		Organization organization = syncOrganization(id, token);
		
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
	public Organization update(String id, EventListenerRequest request) {
		Organization instance = get(id);
		
		logger.info(request.getObject());
		logger.info(String.valueOf(request.getOnCreate()));
		logger.info(String.valueOf(request.getOnDelete()));
		logger.info(String.valueOf(request.getOnUpdate()));
		logger.info(String.valueOf(request.getOnUndelete()));
		
		return instance;
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
	
	private Organization syncOrganization(String id, Token token) {
		
		Organization instance = get(id);
		
		ExecutorService executor = Executors.newFixedThreadPool(2);
		
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
		
		try {
			
			Organization organization = Organization.builder()
					.from(instance)
					.dashboard(Dashboard.of(token))
					.connection(Connection.builder()
							.connectedAs(getIdentityTask.get().getUsername())
							.connectedAt(getCurrentDateTime())
							.id(getIdentityTask.get().getId())
							.instanceUrl(token.getInstanceUrl())
							.isConnected(Boolean.TRUE)
							.issuedAt(token.getIssuedAt())
							.refreshToken(token.getRefreshToken() != null ? token.getRefreshToken() : instance.getConnection().getRefreshToken())
							.status(Connection.CONNECTED)
							.tokenType(token.getTokenType())
							.build())
					.domain(getOrganizationTask.get().getId())
					.name(getOrganizationTask.get().getName())
					.organizationType(getOrganizationTask.get().getOrganizationType())
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
					.build();
			
			return organization;
			
		} catch (InterruptedException | ExecutionException e) {
			logger.severe("Unable to sync organization: " + e.getMessage());
			return instance;
		}
	}
	
	private Organization create(Organization organization) {
		com.nowellpoint.console.entity.Organization entity = modelMapper.map(organization, com.nowellpoint.console.entity.Organization.class);
		entity.setCreatedBy(new com.nowellpoint.console.entity.Identity(UserContext.get() != null ? UserContext.get().getId() : getSystemAdmin().getId().toString()));
		entity.setLastUpdatedBy(entity.getCreatedBy());
		dao.save(entity);
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