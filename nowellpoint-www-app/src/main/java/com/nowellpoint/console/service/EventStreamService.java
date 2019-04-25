package com.nowellpoint.console.service;

import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

import com.nowellpoint.console.entity.AggregationResult;
import com.nowellpoint.console.model.EventStreamMonitor;
import com.nowellpoint.console.model.FeedItem;

public interface EventStreamService {
	
	public EventStreamMonitor getEventStreamMonitor(String organizationId, String source, ZoneId zoneId);

	public List<FeedItem> getStreamingEventsFeed(String id);
	
	public List<FeedItem> getStreamingEventsFeed(String id, String source);
	
	public List<AggregationResult> getEventsLastDays(String id, Integer days, TimeZone timeZone);
	
	public List<AggregationResult> getEventsBySourceByDays(String id, String source, Integer days, TimeZone timeZone);
}