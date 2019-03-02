package com.nowellpoint.console.service;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

import com.nowellpoint.console.entity.AggregationResult;
import com.nowellpoint.console.model.AddressRequest;
import com.nowellpoint.console.model.ContactRequest;
import com.nowellpoint.console.model.CreditCardRequest;
import com.nowellpoint.console.model.FeedItem;
import com.nowellpoint.console.model.StreamingEventListenerRequest;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.OrganizationRequest;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.SubscriptionRequest;

public interface OrganizationService {
	
	public Organization get(String id);
	
	public Organization create(OrganizationRequest request);
	
	public Organization update(String id, String authorizationCode);
	
	public Organization update(String id, StreamingEventListenerRequest request);
	
	public Organization update(String id, CreditCardRequest request);
	
	public Organization setPlan(String id, Plan plan);
	
	public Organization setPlan(String id, Plan plan, CreditCardRequest request);
	
	public Organization update(String id, AddressRequest request);
	
	public Organization update(String id, ContactRequest request);
	
	public Organization update(String id, SubscriptionRequest request);
	
	public Organization refresh(String id);
	
	public void delete(String id);
	
	public byte[] createInvoice(String id, String invoiceNumber) throws IOException;
	
	public List<FeedItem> getStreamingEventsFeed(String id);
	
	public List<FeedItem> getStreamingEventsFeed(String id, String source);
	
	public List<AggregationResult> getEventsLastDays(String id, Integer days, TimeZone timeZone);
	
	public List<AggregationResult> getEventsBySourceByDays(String id, String source, Integer days);
}