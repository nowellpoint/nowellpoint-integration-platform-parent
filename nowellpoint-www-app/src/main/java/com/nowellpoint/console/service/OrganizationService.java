package com.nowellpoint.console.service;

import java.io.IOException;
import java.util.List;

import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.console.entity.AggregationResult;
import com.nowellpoint.console.model.AddressRequest;
import com.nowellpoint.console.model.ContactRequest;
import com.nowellpoint.console.model.CreditCardRequest;
import com.nowellpoint.console.model.EventListenerRequest;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.OrganizationRequest;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.SubscriptionRequest;

public interface OrganizationService {
	
	public Organization get(String id);
	
	public Organization create(OrganizationRequest request);
	
	public Organization update(String id, String authorizationCode);
	
	public Organization update(String id, Token token);
	
	public Organization update(String id, EventListenerRequest request);
	
	public Organization update(String id, CreditCardRequest request);
	
	public Organization setPlan(String id, Plan plan);
	
	public Organization setPlan(String id, Plan plan, CreditCardRequest request);
	
	public Organization update(String id, AddressRequest request);
	
	public Organization update(String id, ContactRequest request);
	
	public Organization update(String id, SubscriptionRequest request);
	
	public void delete(String id);
	
	public byte[] createInvoice(String id, String invoiceNumber) throws IOException;
	
	public List<AggregationResult> getEventsLastDays(String id, Integer days);
}