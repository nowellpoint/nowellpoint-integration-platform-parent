package com.nowellpoint.console.model;

import java.util.Date;
import java.util.Set;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractSubscriptionRequest {
	public abstract Date getNextBillingDate();
	public abstract Date getBillingPeriodStartDate();
	public abstract Date getBillingPeriodEndDate();
	public abstract Set<Transaction> getTransactions();
}