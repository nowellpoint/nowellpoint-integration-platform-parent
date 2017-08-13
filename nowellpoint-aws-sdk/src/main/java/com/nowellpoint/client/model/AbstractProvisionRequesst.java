package com.nowellpoint.client.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractProvisionRequesst {
	public abstract String getCardholderName(); 
	public abstract String getExpirationMonth(); 
	public abstract String getExpirationYear();
	public abstract String getNumber(); 
	public abstract String getCvv();
}