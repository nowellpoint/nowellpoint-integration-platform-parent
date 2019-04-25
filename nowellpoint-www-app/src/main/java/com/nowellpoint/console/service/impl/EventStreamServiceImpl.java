package com.nowellpoint.console.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;

import com.nowellpoint.console.entity.AggregationResult;
import com.nowellpoint.console.entity.StreamingEventDAO;
import com.nowellpoint.console.model.EventStreamMonitor;
import com.nowellpoint.console.model.FeedItem;
import com.nowellpoint.console.service.AbstractService;
import com.nowellpoint.console.service.EventStreamService;

public class EventStreamServiceImpl extends AbstractService implements EventStreamService {
	
	private StreamingEventDAO dao;
	
	public EventStreamServiceImpl() {
		dao = new StreamingEventDAO(com.nowellpoint.console.entity.StreamingEvent.class, datastore);
	}
	
	@Override
	public EventStreamMonitor getEventStreamMonitor(String organizationId, String source, ZoneId zoneId) {
		
		LocalDate today = LocalDate.now( zoneId );
		LocalDate firstDayOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
		LocalDate firstDayOfMonth = LocalDate.of(today.getYear(), today.getMonth(), 1);
		LocalDate firstDayOfYear = LocalDate.of(today.getYear(), 1, 1);
		
		ExecutorService executor = Executors.newFixedThreadPool(4);
		
		Future<Long> todayTask = executor.submit(() -> {
			return dao.streamingEventsCount(new ObjectId(organizationId), source, today, zoneId);
        });
		
		Future<Long> thisWeekTask = executor.submit(() -> {
			return dao.streamingEventsCount(new ObjectId(organizationId), source, firstDayOfWeek, zoneId);
        });
		
		Future<Long> thisMonthTask = executor.submit(() -> {
			return dao.streamingEventsCount(new ObjectId(organizationId), source, firstDayOfMonth, zoneId);
        });
		
		Future<Long> thisYearTask = executor.submit(() -> {
			return dao.streamingEventsCount(new ObjectId(organizationId), source, firstDayOfYear, zoneId);
        });
		
		EventStreamMonitor monitor = null;
		
		try {
			
			monitor = EventStreamMonitor.builder()
					.countToday(todayTask.get())
					.countThisWeek(thisWeekTask.get())
					.countThisMonth(thisMonthTask.get())
					.countThisYear(thisYearTask.get())
					.build();
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}
		
		return monitor;
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