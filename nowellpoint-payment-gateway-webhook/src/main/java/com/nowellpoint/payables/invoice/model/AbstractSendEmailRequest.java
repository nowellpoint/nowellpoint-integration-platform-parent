package com.nowellpoint.payables.invoice.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", deepImmutablesDetection = true)
public abstract class AbstractSendEmailRequest {
	public abstract String getCustomerId();
	public abstract String getTo();
	public abstract String getName();
	public abstract String getInvoiceNumber();
	public abstract String getEncodedContent();
	public abstract String getApiKey();
	public abstract String getApplicationHostname();
}