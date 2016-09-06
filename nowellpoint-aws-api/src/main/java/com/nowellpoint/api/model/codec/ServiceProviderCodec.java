package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.ServiceProvider;
import com.nowellpoint.aws.data.AbstractCodec;

public class ServiceProviderCodec extends AbstractCodec<ServiceProvider> {
	
	public ServiceProviderCodec() {
		super(ServiceProvider.class);
	}
}