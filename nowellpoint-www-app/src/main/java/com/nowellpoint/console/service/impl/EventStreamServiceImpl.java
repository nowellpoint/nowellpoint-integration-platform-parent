package com.nowellpoint.console.service.impl;

import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;

import com.nowellpoint.console.entity.AggregationResult;
import com.nowellpoint.console.entity.StreamingEventDAO;
import com.nowellpoint.console.model.FeedItem;
import com.nowellpoint.console.service.AbstractService;
import com.nowellpoint.console.service.EventStreamService;

public class EventStreamServiceImpl extends AbstractService implements EventStreamService {
	
	private StreamingEventDAO dao;
	
	public EventStreamServiceImpl() {
		dao = new StreamingEventDAO(com.nowellpoint.console.entity.StreamingEvent.class, datastore);
	}

	@Override
	public List<AggregationResult> getEventsLastDays(String id, Integer days, TimeZone timeZone) {
		return dao.getEventsLastDays(new ObjectId(id), days, timeZone);
	}
	
	@Override
	public List<AggregationResult> getEventsBySourceByDays(String id, String source, Integer days, TimeZone timeZone) {
		return dao.getEventsBySourceByDays(new ObjectId(id), source, days, timeZone);
	}
	
	@Override
	public List<FeedItem> getStreamingEventsFeed(String id) {
		return dao.getStreamingEvents(new ObjectId(id))
				.stream()
				.map(s -> FeedItem.of(s))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<FeedItem> getStreamingEventsFeed(String id, String source){
		return dao.getStreamingEventsBySource(new ObjectId(id), source)
				.stream()
				.map(s -> FeedItem.of(s))
				.collect(Collectors.toList());
	}
}