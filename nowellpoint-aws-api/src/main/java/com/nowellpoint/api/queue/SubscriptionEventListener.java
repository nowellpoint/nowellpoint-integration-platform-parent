package com.nowellpoint.api.queue;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.api.rest.domain.Organization;
import com.nowellpoint.api.util.EnvUtil;
import com.nowellpoint.api.util.EnvUtil.Variable;
import com.nowellpoint.aws.data.CacheManager;
import com.nowellpoint.aws.data.QueueListener;
import com.nowellpoint.mongodb.Datastore;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.mongodb.document.MongoDocument;

@QueueListener(queueName="PAYMENT_GATEWAY_INBOUND")
public class SubscriptionEventListener implements MessageListener {
	
	private static final Logger LOGGER = Logger.getLogger(SubscriptionEventListener.class);
	private static final DocumentManagerFactory documentManagerFactory = Datastore.getCurrentSession();
	private static final DocumentManager documentManager = documentManagerFactory.createDocumentManager();

	@Override
	public void onMessage(Message message) {
		
		TextMessage textMessage = (TextMessage) message;
		
		try {
			
			if (textMessage.getStringProperty("WEBHOOK_NOTIFICATION_INSTANCE").equals(EnvUtil.getValue(Variable.BRAINTREE_ENVIRONMENT))) {
				JsonNode subscriptionEvent = new ObjectMapper().readValue(textMessage.getText(), JsonNode.class);
				
				String id = subscriptionEvent.get("id").asText();
				
				LOGGER.info(String.format("Recevied SubscriptionEvent for Subscription %s", id));
				
				com.nowellpoint.api.model.document.Organization document = documentManager.findOne(com.nowellpoint.api.model.document.Organization.class, eq ( "subscription.number", id ));
				
				Organization organization = Organization.of(document, subscriptionEvent);
				
				updateOrganization(organization);
				
				message.acknowledge();
			}
			
		} catch (JMSException | IOException | DocumentNotFoundException e) {
			LOGGER.error(e);
		}
	}
	
	private void updateOrganization(Organization organization) {
		MongoDocument document = organization.toDocument();
		documentManager.replaceOne( document );
		organization.replace( document );
		CacheManager.set(document.getId().toString(), document);
	}
}