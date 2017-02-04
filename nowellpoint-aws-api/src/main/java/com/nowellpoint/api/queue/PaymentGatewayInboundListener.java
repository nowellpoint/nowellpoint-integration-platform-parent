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
import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.CreditCard;
import com.nowellpoint.api.rest.domain.Transaction;
import com.nowellpoint.api.rest.service.AccountProfileServiceImpl;
import com.nowellpoint.api.service.EmailService;
import com.nowellpoint.aws.data.QueueListener;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.util.Properties;

@QueueListener(queueName="PAYMENT_GATEWAY_INBOUND")
public class PaymentGatewayInboundListener implements MessageListener {
	
	private static final Logger LOGGER = Logger.getLogger(PaymentGatewayInboundListener.class);
	
	private AccountProfileServiceImpl accountProfileServiceImpl = new AccountProfileServiceImpl();
	private EmailService emailService = new EmailService();

	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			System.out.println(textMessage.getStringProperty("WEBHOOK_NOTIFICATION_INSTANCE"));
			System.out.println(textMessage.getStringProperty("WEBHOOK_NOTIFICATION_KIND"));
			
			if (textMessage.getStringProperty("WEBHOOK_NOTIFICATION_INSTANCE").equals(System.getProperty(Properties.BRAINTREE_ENVIRONMENT))) {
				
				JsonNode subscription = new ObjectMapper().readValue(textMessage.getText(), JsonNode.class);
				JsonNode transactions = subscription.get("transactions");
				
				try {
					AccountProfile accountProfile = accountProfileServiceImpl.findBySubscriptionId(subscription.get("id").asText());
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
					
					accountProfileServiceImpl.updateAccountProfile(accountProfile);
					
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