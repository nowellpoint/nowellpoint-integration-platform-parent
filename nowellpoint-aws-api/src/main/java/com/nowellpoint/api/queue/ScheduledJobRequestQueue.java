package com.nowellpoint.api.queue;

import javax.jms.Message;
import javax.jms.MessageListener;

import com.nowellpoint.aws.data.SimpleQueueListener;

@SimpleQueueListener(queueName="SCHEDULED_JOB_REQUEST_QUEUE")
public class ScheduledJobRequestQueue implements MessageListener {

	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		
	}
}