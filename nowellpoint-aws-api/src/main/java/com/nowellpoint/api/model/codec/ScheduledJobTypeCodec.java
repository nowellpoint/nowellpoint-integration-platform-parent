package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.ScheduledJobType;
import com.nowellpoint.aws.data.AbstractCodec;

public class ScheduledJobTypeCodec extends AbstractCodec<ScheduledJobType> {
	
	public ScheduledJobTypeCodec() {
		super(ScheduledJobType.class);
	}
}