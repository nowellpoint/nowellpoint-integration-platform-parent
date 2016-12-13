package com.nowellpoint.api.queue;

import javax.jms.Message;
import javax.jms.MessageListener;

import com.nowellpoint.aws.data.QueueListener;

@QueueListener(queueName="SCHEDULED_JOB_REQUEST_QUEUE")
public class ScheduledJobRequestQueue implements MessageListener {

	@Override
	public void onMessage(Message message) {
		
		
	}
}