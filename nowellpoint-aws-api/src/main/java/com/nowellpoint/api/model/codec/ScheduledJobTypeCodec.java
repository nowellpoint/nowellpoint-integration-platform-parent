package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.ScheduledJobType;
import com.nowellpoint.mongodb.document.AbstractCodec;

public class ScheduledJobTypeCodec extends AbstractCodec<ScheduledJobType> {
	
	public ScheduledJobTypeCodec() {
		super(ScheduledJobType.class);
	}
}