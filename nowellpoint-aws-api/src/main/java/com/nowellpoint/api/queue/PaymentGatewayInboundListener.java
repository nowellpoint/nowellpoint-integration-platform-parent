package com.nowellpoint.api.queue;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.api.model.document.AccountProfile;
import com.nowellpoint.api.model.document.CreditCard;
import com.nowellpoint.api.model.document.Transaction;
import com.nowellpoint.api.rest.service.SendGridEmailService;
import com.nowellpoint.api.service.EmailService;
import com.nowellpoint.aws.data.CacheManager;
import com.nowellpoint.aws.data.QueueListener;
import com.nowellpoint.mongodb.Datastore;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.util.Properties;

@QueueListener(queueName="PAYMENT_GATEWAY_INBOUND")
public class PaymentGatewayInboundListener implements MessageListener {
	
	private static final Logger LOGGER = Logger.getLogger(PaymentGatewayInboundListener.class);
	
	private EmailService emailService = new SendGridEmailService();

	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			
			if (textMessage.getStringProperty("WEBHOOK_NOTIFICATION_INSTANCE").equals(System.getProperty(Properties.BRAINTREE_ENVIRONMENT))) {
				
				JsonNode subscription = new ObjectMapper().readValue(textMessage.getText(), JsonNode.class);
				JsonNode transactions = subscription.get("transactions");
				
				LOGGER.info(String.format("Payment Gateway Event received: %s for Subscription Id: %s",
						textMessage.getStringProperty("WEBHOOK_NOTIFICATION_KIND"),
						subscription.get("id").asText()));

				try {
					
					DocumentManagerFactory documentManagerFactory = Datastore.getCurrentSession();
					DocumentManager documentManager = documentManagerFactory.createDocumentManager();
					AccountProfile accountProfile = documentManager.findOne(AccountProfile.class, eq ( "subscription.subscriptionId", subscription.get("id").asText() ) );
					
					accountProfile.getSubscription().setStatus(subscription.get("status").asText());
					accountProfile.getSubscription().setNextBillingDate(new Date(subscription.get("nextBillingDate").asLong()));
					accountProfile.getSubscription().setUpdatedOn(Date.from(Instant.now()));
					
					if (transactions.isArray()) {
						
						for (JsonNode t : transactions) {
							
							Optional<Transaction> optional = accountProfile.getTransactions()
									.stream()
									.filter(transaction -> transaction.getId().equals(t.get("id").asText()))
									.findAny();
							
							if (! optional.isPresent()) {
								
								Transaction transaction = new Transaction();
								transaction.setId(t.get("id").asText());
								transaction.setAmount(t.get("amount").asDouble());
								transaction.setCreatedOn(new Date(t.get("createdAt").asLong()));
								transaction.setCurrencyIsoCode(t.get("currencyIsoCode").asText());
								transaction.setStatus(t.get("status").asText());
								transaction.setUpdatedOn(new Date(t.get("updatedAt").asLong()));
								
								CreditCard creditCard = new CreditCard();
								creditCard.setLastFour(t.get("creditCard").get("last4").asText());
								creditCard.setCardType(t.get("creditCard").get("cardType").asText());
								creditCard.setCardholderName(t.get("creditCard").get("cardholderName").asText());
								creditCard.setExpirationMonth(t.get("creditCard").get("expirationMonth").asText());
								creditCard.setExpirationYear(t.get("creditCard").get("expirationYear").asText());
								creditCard.setImageUrl(t.get("creditCard").get("imageUrl").asText());
								creditCard.setToken(t.get("creditCard").get("token").asText());
								
								transaction.setCreditCard(creditCard);
								
								accountProfile.addTransaction(transaction);
							}
						}
					}
					
					documentManager.replaceOne( accountProfile );
					
					CacheManager.del( accountProfile.getId().toString() );
					
					/**
					 * WebhookNotification.Kind.SUBSCRIPTION_CANCELED
				WebhookNotification.Kind.SUBSCRIPTION_CHARGED_SUCCESSFULLY
				WebhookNotification.Kind.SUBSCRIPTION_CHARGED_UNSUCCESSFULLY
				WebhookNotification.Kind.SUBSCRIPTION_EXPIRED
				WebhookNotification.Kind.SUBSCRIPTION_TRIAL_ENDED
				WebhookNotification.Kind.SUBSCRIPTION_WENT_ACTIVE
				WebhookNotification.Kind.SUBSCRIPTION_WENT_PAST_DUE
				
					 */
					
					emailService.sendInvoiceMessage(accountProfile.getEmail(), accountProfile.getName());
					
				} catch (DocumentNotFoundException e) {
					LOGGER.error(e);
				} finally {
					message.acknowledge();
				}
			}
			
		} catch (JMSException | IOException e) {
			LOGGER.error(e);
		}
	}
}