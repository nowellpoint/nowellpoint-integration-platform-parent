package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.ScheduledJobRunDetail;
import com.nowellpoint.mongodb.document.AbstractCodec;

public class ScheduledJobRunDetailCodec extends AbstractCodec<ScheduledJobRunDetail> {
	
	public ScheduledJobRunDetailCodec() {
		super(ScheduledJobRunDetail.class);
	}
}