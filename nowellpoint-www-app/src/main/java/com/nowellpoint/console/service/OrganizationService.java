package com.nowellpoint.console.service;

import java.io.IOException;

import com.nowellpoint.console.model.AddressRequest;
import com.nowellpoint.console.model.ContactRequest;
import com.nowellpoint.console.model.CreditCardRequest;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.Subscription;

public interface OrganizationService {
	
	public Organization get(String id);
	
	public Organization update(String id, CreditCardRequest request);
	
	public Organization setPlan(String id, Plan plan);
	
	public Organization setPlan(String id, Plan plan, CreditCardRequest request);
	
	public Organization update(String id, AddressRequest request);
	
	public Organization update(String id, ContactRequest request);
	
	public Organization update(String id, Subscription subscription);
	
	public byte[] createInvoice(String id, String invoiceNumber) throws IOException;
}