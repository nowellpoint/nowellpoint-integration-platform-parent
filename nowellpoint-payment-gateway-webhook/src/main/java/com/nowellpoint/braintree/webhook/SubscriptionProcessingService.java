package com.nowellpoint.braintree.webhook;

import java.util.List;
import java.util.Locale;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Customer;
import com.braintreegateway.Environment;
import com.braintreegateway.Plan;
import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.braintreegateway.WebhookNotification;
import com.nowellpoint.payables.invoice.InvoiceGenerator;
import com.nowellpoint.payables.invoice.model.Invoice;
import com.nowellpoint.payables.invoice.model.Payee;
import com.nowellpoint.payables.invoice.model.PaymentMethod;
import com.nowellpoint.payables.invoice.model.Service;

public class SubscriptionProcessingService {
	
	private static final DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	public String handleEvent(DynamodbEvent event, Context context) {
    	
    	LambdaLogger logger = context.getLogger();
		
    	event.getRecords().stream().filter(record -> "INSERT".equals(record.getEventName())).forEach(record -> {
    		
    		logger.log("DynamodbEvent received...Event Id: "
					.concat(record.getEventID())
					.concat(" Event Name: " + record.getEventName()));
    		
    		String id = record.getDynamodb().getKeys().get("Id").getS();
    		
    		PaymentGatewayNotification notification = mapper.load(PaymentGatewayNotification.class, id);
    		
    		BraintreeGateway gateway = new BraintreeGateway(
    				Environment.parseEnvironment(notification.getEnvironment()),
    				notification.getMerchantId(),
    				notification.getPublicKey(),
    				notification.getPrivateKey()
    		);
    		
    		gateway.clientToken().generate();
    		
    		Subscription subscription = gateway.subscription().find(id);
			
			Transaction transaction = subscription.getTransactions()
					.stream().sorted((t1,t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
					.findFirst()
					.get();
    		
			if (notification.equals(WebhookNotification.Kind.SUBSCRIPTION_CHARGED_SUCCESSFULLY.name())) {
				
				List<Plan> plans = gateway
						.plan()
						.all();
				
				Plan plan = plans.stream()
						.filter(p -> p.getId().equals(transaction.getPlanId()))
						.findFirst()
						.get();
				
				Customer customer = gateway
						.customer()
						.find(transaction.getCustomer().getId());
				
				Payee payee = Payee.builder()
						.customerId(customer.getId())
						.attentionTo(transaction.getCreditCard().getCardholderName())
						.email(customer.getEmail())
						.city(customer.getAddresses().get(0).getLocality())
						.companyName(customer.getCompany())
						.country(customer.getAddresses().get(0).getCountryName())
						.postalCode(customer.getAddresses().get(0).getPostalCode())
						.state(customer.getAddresses().get(0).getRegion())
						.street(customer.getAddresses().get(0).getStreetAddress())
						.build();
				
				PaymentMethod paymentMethod = PaymentMethod.builder()
						.cardType(transaction.getCreditCard().getCardType())
						.lastFour(transaction.getCreditCard().getLast4())
						.build();
				
				Service service = Service.builder()
						.quantity(1)
						.serviceName(plan.getName())
						.totalPrice(transaction.getAmount())
						.unitPrice(plan.getPrice())
						.build();
				
				Invoice invoice = Invoice.builder()
						.billingPeriodEndDate(subscription.getBillingPeriodEndDate().getTime())
						.billingPeriodStartDate(subscription.getBillingPeriodStartDate().getTime())
						.invoiceNumber(transaction.getId())
						.locale(Locale.US)
						.transactionDate(transaction.getCreatedAt().getTime())
						.payee(payee)
						.paymentMethod(paymentMethod)
						.addServices(service)
						.build();
				
				new InvoiceGenerator().generate(invoice);
			}
			
			notification.setStatus(PaymentGatewayNotification.Status.PROCESSED.name());
    	});
		
    	return "ok";
	}
}
