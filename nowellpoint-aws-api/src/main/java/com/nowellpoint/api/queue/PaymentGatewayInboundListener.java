package com.nowellpoint.api.queue;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;

import com.braintreegateway.Subscription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.api.model.domain.AccountProfile;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.aws.data.QueueListener;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;

@QueueListener(queueName="PAYMENT_GATEWAY_INBOUND")
public class PaymentGatewayInboundListener implements MessageListener {
	
	private static final Logger LOGGER = Logger.getLogger(PaymentGatewayInboundListener.class);
	
	private AccountProfileService accountProfileService = new AccountProfileService();

	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			System.out.println(textMessage.getText());
			System.out.println(textMessage.getStringProperty("WEBHOOK_NOTIFICATION_INSTANCE"));
			System.out.println(textMessage.getStringProperty("WEBHOOK_NOTIFICATION_KIND"));
			
			Subscription subscription = new ObjectMapper().readValue(textMessage.getText(), Subscription.class);
			
			AccountProfile accountProfile = accountProfileService.findBySubscriptionId(subscription.getId());
			
			System.out.println(accountProfile.getName());
			
		} catch (JMSException | IOException | DocumentNotFoundException e) {
			LOGGER.error(e);
		}
	}
}