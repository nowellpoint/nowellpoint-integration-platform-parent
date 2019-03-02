package com.nowellpoint.console.entity;

import static org.mongodb.morphia.aggregation.Group.grouping;
import static org.mongodb.morphia.aggregation.Group.id;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.bson.BSONObject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.aggregation.Accumulator;
import org.mongodb.morphia.aggregation.AggregationPipeline;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;

import com.mongodb.AggregationOptions;

public class OrganizationDAO extends BasicDAO<Organization, ObjectId> {

	public OrganizationDAO(Class<Organization> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
	
	public List<StreamingEvent> getStreamingEvents(ObjectId organizationId, TimeZone timeZone) {
		
		FindOptions options = new FindOptions().limit(50);
		
		List<StreamingEvent> streamingEventsList = getDatastore().createQuery(StreamingEvent.class)
				.field("organizationId")
				.equal(organizationId)
				.order("-eventDate")
				.asList(options);
		
		return streamingEventsList;
	}

	public List<StreamingEvent> getStreamingEventsBySource(ObjectId organizationId, String source) {
		
		FindOptions options = new FindOptions().limit(50);
		
		List<StreamingEvent> streamingEventsList = getDatastore().createQuery(StreamingEvent.class)
				.field("organizationId")
				.equal(organizationId)
				.field("source")
				.equal(source)
				.order("-eventDate")
				.asList(options);
		
		return streamingEventsList;
	}
	
	public List<AggregationResult> getEventsBySourceByDays(ObjectId organizationId, String source, Integer days) {
		
		ZoneId utc = ZoneId.of( "UTC" );
		
		LocalDate startDate = LocalDate.now(utc).minusDays(days);
		
		Query<StreamingEvent> query = getDatastore().createQuery(StreamingEvent.class)
				.field("organizationId")
				.equal(organizationId)
				.field("source")
				.equal(source)
				.field("eventDate")
				.greaterThanOrEq(Date.from(startDate.atStartOfDay()
					      .atZone(utc)
					      .toInstant()));
		
		return eventDateAggregator(days, query);
	}
	
	public List<AggregationResult> getEventsLastDays(ObjectId organizationId, Integer days, TimeZone timeZone) {
		
		ZoneId zoneId = ZoneId.of( timeZone.getID() );
		
		LocalDate startDate = LocalDate.now(zoneId).minusDays(days);
		
		Query<StreamingEvent> query = getDatastore().createQuery(StreamingEvent.class)
				.field("organizationId")
				.equal(organizationId)
				.field("eventDate")
				.greaterThanOrEq(Date.from(startDate.atStartOfDay()
					      .atZone(zoneId)
					      .toInstant()));
		
		return eventDateAggregator(days, query);
	}
	
	private List<AggregationResult> eventDateAggregator(Integer days, Query<StreamingEvent> query) {
		
		ZoneId utc = ZoneId.of( "UTC" );
		
		LocalDate startDate = LocalDate.now(utc).minusDays(days);
		LocalDate today = LocalDate.now(utc);
		
		AggregationOptions options = AggregationOptions.builder()
                .outputMode(AggregationOptions.OutputMode.CURSOR)
                .build();
		
		AggregationPipeline pipeline = getDatastore().createAggregation(StreamingEvent.class)
				.match(query)
				.group(
						id(
								grouping("year", new Accumulator("$year", "eventDate")),
								grouping("month", new Accumulator("$month", "eventDate")),
								grouping("day", new Accumulator("$dayOfMonth", "eventDate"))
						),
						grouping("count", new Accumulator("$sum", 1))
				);
		
		Iterator<AggregationResult> iterator = pipeline.aggregate(AggregationResult.class, options);
		
		Long numberOfDays = ChronoUnit.DAYS.between(startDate, today) + 1;
		
		AtomicReference<LocalDate> referenceDate = new AtomicReference<LocalDate>(today);
		
		Map<String,AggregationResult> buckets = new HashMap<String,AggregationResult>();
		
		IntStream.range(0, numberOfDays.intValue()).forEach( i -> {
			String key = String.valueOf(referenceDate.get().getYear())
					.concat(String.valueOf(referenceDate.get().getMonthValue()))
					.concat(String.valueOf(referenceDate.get().getDayOfMonth()));
					
			buckets.put(key, new AggregationResult(String.valueOf(i), referenceDate.get(), new Long(0)));
			
			referenceDate.set(referenceDate.get().minusDays(1));
		});
		
		StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false).forEach( r -> {
			BSONObject bson = (BSONObject)com.mongodb.util.JSON.parse(r.getId());
			
			String key = bson.get("year").toString()
					.concat(bson.get("month").toString())
					.concat(bson.get("day").toString());
			
			if (buckets.containsKey(key)) {
				buckets.get(key).setCount(r.getCount());
			}
		});
		
		return buckets.values()
				.stream()
				.collect(Collectors.toList());
	}
}