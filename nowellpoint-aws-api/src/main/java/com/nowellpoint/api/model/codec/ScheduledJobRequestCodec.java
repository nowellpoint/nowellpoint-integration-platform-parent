package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.ScheduledJobRequest;
import com.nowellpoint.mongodb.document.AbstractCodec;

public class ScheduledJobRequestCodec extends AbstractCodec<ScheduledJobRequest> {
	
	public ScheduledJobRequestCodec() {
		super(ScheduledJobRequest.class);
	}
}