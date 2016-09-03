package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.ServiceProviderDocument;
import com.nowellpoint.aws.data.AbstractCodec;

public class ServiceProviderCodec extends AbstractCodec<ServiceProviderDocument> {
	
	public ServiceProviderCodec() {
		super(ServiceProviderDocument.class);
	}
}