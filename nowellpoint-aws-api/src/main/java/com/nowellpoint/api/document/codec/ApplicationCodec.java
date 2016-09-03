package com.nowellpoint.api.document.codec;

import com.nowellpoint.api.model.Application;
import com.nowellpoint.aws.data.AbstractCodec;

public class ApplicationCodec extends AbstractCodec<Application> {
	
	public ApplicationCodec() {
		super(Application.class);
	}
}