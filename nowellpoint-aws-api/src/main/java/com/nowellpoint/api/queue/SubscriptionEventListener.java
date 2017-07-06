package com.nowellpoint.api.queue;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
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
import com.nowellpoint.aws.data.CacheManager;
import com.nowellpoint.aws.data.QueueListener;
import com.nowellpoint.mongodb.Datastore;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.util.Properties;

@QueueListener(queueName="PAYMENT_GATEWAY_INBOUND")
public class SubscriptionEventListener implements MessageListener {
	
	private static final Logger LOGGER = Logger.getLogger(SubscriptionEventListener.class);
	private static final DocumentManagerFactory documentManagerFactory = Datastore.getCurrentSession();
	private static final DocumentManager documentManager = documentManagerFactory.createDocumentManager();

	@Override
	public void onMessage(Message message) {
		
		TextMessage textMessage = (TextMessage) message;
		
		try {
			
			if (textMessage.getStringProperty("WEBHOOK_NOTIFICATION_INSTANCE").equals(System.getProperty(Properties.BRAINTREE_ENVIRONMENT))) {
				JsonNode node = new ObjectMapper().readValue(textMessage.getText(), JsonNode.class);
				
				String id = node.get("id").asText();
				
				LOGGER.info(String.format("Recevied SubscriptionEvent for Subscription %s", id));
				
				AccountProfile accountProfile = documentManager.findOne(AccountProfile.class, eq ( "subscription.subscriptionId", id ));
				
				accountProfile.getSubscription().setStatus(node.get("status").asText());
				accountProfile.getSubscription().setNextBillingDate(new Date(node.get("nextBillingDate").asLong()));
				
				Optional<Transaction> optional = accountProfile.getTransactions()
						.stream()
						.filter(transaction -> transaction.getId().equals(node.get("transaction").get("id").asText()))
						.findAny();
				
				if (! optional.isPresent()) {
					
					Transaction transaction = new Transaction();
					transaction.setId(node.get("transaction").get("id").asText());
					transaction.setAmount(node.get("transaction").get("amount").asDouble());
					transaction.setCreatedOn(new Date(node.get("transaction").get("createdAt").asLong()));
					transaction.setCurrencyIsoCode(node.get("transaction").get("currencyIsoCode").asText());
					transaction.setStatus(node.get("transaction").get("status").asText());
					transaction.setUpdatedOn(new Date(node.get("transaction").get("updatedAt").asLong()));
					
					CreditCard creditCard = new CreditCard();
					
					if (! node.get("transaction").get("creditCard").isNull()) {
						creditCard.setLastFour(node.get("transaction").get("creditCard").get("last4").asText());
						creditCard.setCardType(node.get("transaction").get("creditCard").get("cardType").asText());
						creditCard.setCardholderName(node.get("transaction").get("creditCard").get("cardholderName").asText());
						creditCard.setExpirationMonth(node.get("transaction").get("creditCard").get("expirationMonth").asText());
						creditCard.setExpirationYear(node.get("transaction").get("creditCard").get("expirationYear").asText());
						creditCard.setImageUrl(node.get("transaction").get("creditCard").get("imageUrl").asText());
						creditCard.setToken(node.get("transaction").get("creditCard").get("token").asText());
					}
							
					transaction.setCreditCard(creditCard);
					 
					accountProfile.addTransaction(transaction);
					
				}
				
				documentManager.replaceOne(accountProfile);
				
				CacheManager.set(accountProfile.getId().toString(), accountProfile);
				
				message.acknowledge();
			}
			
		} catch (JMSException | IOException | DocumentNotFoundException e) {
			LOGGER.error(e);
		}
	}
}