package com.nowellpoint.api.queue;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Customer;
import com.braintreegateway.Environment;
import com.braintreegateway.Plan;
import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.braintreegateway.WebhookNotification;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.api.invoice.InvoiceGenerator;
import com.nowellpoint.api.invoice.model.Invoice;
import com.nowellpoint.api.invoice.model.Payee;
import com.nowellpoint.api.invoice.model.PaymentMethod;
import com.nowellpoint.api.invoice.model.Service;
import com.nowellpoint.aws.data.QueueListener;
import com.nowellpoint.util.Properties;

@QueueListener(queueName="PAYMENT_GATEWAY_INBOUND")
public class PaymentGatewayInboundListener implements MessageListener {
	
	/**
	 * WebhookNotification.Kind.SUBSCRIPTION_CANCELED
WebhookNotification.Kind.SUBSCRIPTION_CHARGED_SUCCESSFULLY
WebhookNotification.Kind.SUBSCRIPTION_CHARGED_UNSUCCESSFULLY
WebhookNotification.Kind.SUBSCRIPTION_EXPIRED
WebhookNotification.Kind.SUBSCRIPTION_TRIAL_ENDED
WebhookNotification.Kind.SUBSCRIPTION_WENT_ACTIVE
WebhookNotification.Kind.SUBSCRIPTION_WENT_PAST_DUE

	 */
	
	private static final Logger LOGGER = Logger.getLogger(PaymentGatewayInboundListener.class);
	
	private InvoiceGenerator invoiceGenerator = new InvoiceGenerator();

	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			
			if (textMessage.getStringProperty("WEBHOOK_NOTIFICATION_INSTANCE").equals(System.getProperty(Properties.BRAINTREE_ENVIRONMENT))) {
				
				BraintreeGateway gateway = new BraintreeGateway(
						Environment.parseEnvironment(System.getProperty(Properties.BRAINTREE_ENVIRONMENT)),
						System.getProperty(Properties.BRAINTREE_MERCHANT_ID),
						System.getProperty(Properties.BRAINTREE_PUBLIC_KEY),
						System.getProperty(Properties.BRAINTREE_PRIVATE_KEY)
				);
				
				gateway.clientToken().generate();
				
				JsonNode node = new ObjectMapper().readValue(textMessage.getText(), JsonNode.class);
				
				Subscription subscription = gateway.subscription().find(node.get("id").asText());
				
				LOGGER.info(String.format("Payment Gateway Event received: %s for Subscription Id: %s",
						textMessage.getStringProperty("WEBHOOK_NOTIFICATION_KIND"),
						subscription.getId()));
				
						
					//DocumentManagerFactory documentManagerFactory = Datastore.getCurrentSession();
					//DocumentManager documentManager = documentManagerFactory.createDocumentManager();
					//AccountProfile accountProfile = documentManager.findOne(AccountProfile.class, eq ( "subscription.subscriptionId", subscription.getId() ) );
					
					Transaction transaction = subscription.getTransactions()
							.stream().sorted((t1,t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
							.findFirst()
							.get();
					
					if (textMessage.getStringProperty("WEBHOOK_NOTIFICATION_KIND").equals(WebhookNotification.Kind.SUBSCRIPTION_CHARGED_SUCCESSFULLY.name())) {
						
						List<Plan> plans = gateway.plan().all();
						
						Plan plan = plans.stream().filter(p -> p.getId().equals(transaction.getPlanId())).findFirst().get();
						
						Customer customer = gateway.customer().find(transaction.getCustomer().getId());
						
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
						
						invoiceGenerator.generate(invoice);
						
						message.acknowledge();
					}
//						
//					accountProfile.getSubscription().setStatus(subscription.get("status").asText());
//					accountProfile.getSubscription().setNextBillingDate(new Date(subscription.get("nextBillingDate").asLong()));
//					accountProfile.getSubscription().setUpdatedOn(Date.from(Instant.now()));
//					accountProfile.getSubscription().setBillingPeriodStartDate(new Date(subscription.get("billingPeriodStartDate").asLong()));
//					accountProfile.getSubscription().setBillingPeriodEndDate(new Date(subscription.get("billingPeriodEndDate").asLong()));
//						
//					if (transactions.isArray()) {
//							
//						for (JsonNode t : transactions) {
//								
//							Optional<Transaction> optional = accountProfile.getTransactions()
//									.stream()
//									.filter(transaction -> transaction.getId().equals(t.get("id").asText()))
//									.findAny();
//								
//							if (! optional.isPresent()) {
//									
//								Transaction transaction = new Transaction();
//								transaction.setId(t.get("id").asText());
//								transaction.setAmount(t.get("amount").asDouble());
//								transaction.setCreatedOn(new Date(t.get("createdAt").asLong()));
//								transaction.setCurrencyIsoCode(t.get("currencyIsoCode").asText());
//								transaction.setStatus(t.get("status").asText());
//								transaction.setUpdatedOn(new Date(t.get("updatedAt").asLong()));
//								
//								CreditCard creditCard = new CreditCard();
//								
//								if (! t.get("creditCard").isNull()) {
//									creditCard.setLastFour(t.get("creditCard").get("last4").asText());
//									creditCard.setCardType(t.get("creditCard").get("cardType").asText());
//									creditCard.setCardholderName(t.get("creditCard").get("cardholderName").asText());
//									creditCard.setExpirationMonth(t.get("creditCard").get("expirationMonth").asText());
//									creditCard.setExpirationYear(t.get("creditCard").get("expirationYear").asText());
//									creditCard.setImageUrl(t.get("creditCard").get("imageUrl").asText());
//									creditCard.setToken(t.get("creditCard").get("token").asText());
//								}
//								
//								transaction.setCreditCard(creditCard);
//									
//								accountProfile.addTransaction(transaction);
//								
//								documentManager.replaceOne( accountProfile );
//								
//								CacheManager.del( accountProfile.getId().toString() );
//								
								
//						}
//					}	
								
			}
			
		} catch (JMSException | IOException e) {
			LOGGER.error(e);
		}
	}
}