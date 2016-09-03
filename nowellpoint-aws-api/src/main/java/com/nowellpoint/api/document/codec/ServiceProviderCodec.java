package com.nowellpoint.api.document.codec;

import com.nowellpoint.api.model.ServiceProvider;
import com.nowellpoint.aws.data.AbstractCodec;

public class ServiceProviderCodec extends AbstractCodec<ServiceProvider> {
	
	public ServiceProviderCodec() {
		super(ServiceProvider.class);
	}
}