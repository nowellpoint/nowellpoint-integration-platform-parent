package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.ScheduledJob;
import com.nowellpoint.aws.data.AbstractCodec;

public class ScheduledJobCodec extends AbstractCodec<ScheduledJob> {
	
	public ScheduledJobCodec() {
		super(ScheduledJob.class);
	}
}