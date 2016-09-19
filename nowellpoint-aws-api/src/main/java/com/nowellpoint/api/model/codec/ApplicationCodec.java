package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.Application;
import com.nowellpoint.mongodb.document.AbstractCodec;

public class ApplicationCodec extends AbstractCodec<Application> {
	
	public ApplicationCodec() {
		super(Application.class);
	}
}