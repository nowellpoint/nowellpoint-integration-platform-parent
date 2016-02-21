package com.nowellpoint.aws.event;

import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventStatus;
import com.nowellpoint.aws.model.annotation.Handler;

public class DocumentEventHandler implements AbstractEventHandler {
	
	private static LambdaLogger logger;

	@Override
	public void process(Event event, Map<String, String> properties, Context context) throws Exception {
		
		//
		//
		//
		
		logger = context.getLogger();
		
		//
		//
		//
		
		logger.log(this.getClass().getName() + " starting DataEventHandler");
		
		//
		//
		//
		
		Class<?> type = Class.forName(event.getType());
		if (! type.isAnnotationPresent(Handler.class)) {
			throw new Exception( String.format("Class %s is missing Handler annotation", type.getName()));
		}
		
		//
		//
		//
		
		Handler handler = type.getAnnotation(Handler.class);
		
		//
		//
		//
		
		logger.log(this.getClass().getName() + " route to queue: " + handler.queueName());
		
		//
		//
		//
		
		SQSConnectionFactory connectionFactory = SQSConnectionFactory
				.builder()
				.build();	
		
		logger.log(this.getClass().getName() +  "connected to SQS");
		
		SQSConnection connection = connectionFactory.createConnection();
		
		Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		
		Queue queue = session.createQueue(handler.queueName());
		
		MessageProducer producer = session.createProducer(queue);
		
		Message message = session.createObjectMessage(event);
		
		logger.log(this.getClass().getName() + " sending Identity message for Event: " + event.getId());
		
		producer.send(message);
		
		connection.close();
		
		//
		//
		//
		
		event.setEventStatus(EventStatus.QUEUED.toString());
	}
}