package com.nowellpoint.api.queue;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.api.model.domain.AccountProfile;
import com.nowellpoint.api.model.domain.Transaction;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.aws.data.QueueListener;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.util.Properties;

@QueueListener(queueName="PAYMENT_GATEWAY_INBOUND")
public class PaymentGatewayInboundListener implements MessageListener {
	
	private static final Logger LOGGER = Logger.getLogger(PaymentGatewayInboundListener.class);
	
	private AccountProfileService accountProfileService = new AccountProfileService();

	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			System.out.println(textMessage.getStringProperty("WEBHOOK_NOTIFICATION_INSTANCE"));
			System.out.println(textMessage.getStringProperty("WEBHOOK_NOTIFICATION_KIND"));
			
			if (textMessage.getStringProperty("WEBHOOK_NOTIFICATION_INSTANCE").equals(System.getProperty(Properties.BRAINTREE_ENVIRONMENT))) {
				
				JsonNode subscription = new ObjectMapper().readValue(textMessage.getText(), JsonNode.class);
				JsonNode transactions = subscription.get("transactions");
				
				System.out.println(subscription.get("id").asText());
				
				try {
					AccountProfile accountProfile = accountProfileService.findBySubscriptionId(subscription.get("id").asText());
					accountProfile.getSubscription().setStatus(subscription.get("status").asText());
					accountProfile.getSubscription().setNextBillingDate(new Date(subscription.get("nextBillingDate").asLong()));
					accountProfile.getSubscription().setUpdatedOn(Date.from(Instant.now()));
					
					if (transactions.isArray()) {
						for (JsonNode t : transactions) {
							Transaction transaction = new Transaction();
							transaction.setId(t.get("id").asText());
							transaction.setAmount(t.get("amount").asDouble());
							transaction.setCreatedOn(new Date(t.get("createdAt").asLong()));
							transaction.setCurrencyIsoCode(t.get("currencyIsoCode").asText());
							transaction.setStatus(t.get("status").asText());
							transaction.setUpdatedOn(new Date(t.get("updatedAt").asLong()));
							accountProfile.addTransaction(transaction);
						}
					}
					
					accountProfileService.updateAccountProfile(accountProfile);
					
				} catch (DocumentNotFoundException e) {
					LOGGER.error(e);
				} finally {
					//message.acknowledge();
				}
			}
			
		} catch (JMSException | IOException e) {
			LOGGER.error(e);
		}
	}
}