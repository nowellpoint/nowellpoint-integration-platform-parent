package com.nowellpoint.payables.invoice.model;

import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", deepImmutablesDetection = true)
public abstract class AbstractInvoice {
	public abstract String getInvoiceNumber();
	public abstract Date getTransactionDate();
	public abstract Date getBillingPeriodStartDate();
	public abstract Date getBillingPeriodEndDate();
	public abstract Locale getLocale();
	public abstract Payee getPayee();
	public abstract PaymentMethod getPaymentMethod();
	public abstract Set<Service> getServices();
}