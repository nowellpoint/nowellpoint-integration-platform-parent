package com.nowellpoint.client.model;

import java.util.List;

public abstract class AbstractOrganization {
	public abstract String getNumber();
	public abstract String getName();
	public abstract String getDomain();
	public abstract Subscription getSubscription();
	public abstract List<Transaction> getTransactions();
}