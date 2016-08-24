package com.nowellpoint.aws.api.codec;

import com.nowellpoint.aws.api.model.Application;
import com.nowellpoint.aws.data.AbstractCodec;

public class BatchJobCodec extends AbstractCodec<Application> {
	
	public BatchJobCodec() {
		super(Application.class);
	}
}