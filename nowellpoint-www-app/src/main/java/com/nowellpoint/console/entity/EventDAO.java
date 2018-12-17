package com.nowellpoint.console.entity;

import static org.mongodb.morphia.aggregation.Group.grouping;
import static org.mongodb.morphia.aggregation.Group.id;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.bson.BSONObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.aggregation.Accumulator;
import org.mongodb.morphia.aggregation.AggregationPipeline;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import com.mongodb.AggregationOptions;

public class EventDAO extends BasicDAO<Event,String> {
	
	public EventDAO(Class<Event> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
	
	public List<AggregationResult> getEventsLast7Days() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		
		Calendar startDate = Calendar.getInstance();
		startDate.add(Calendar.DATE, -8);
		startDate.set(Calendar.HOUR, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		
		AggregationOptions options = AggregationOptions.builder()
                .outputMode(AggregationOptions.OutputMode.CURSOR)
                .build();
		
		Query<Event> query = getDatastore().createQuery(Event.class)
				.field("eventDate")
				.greaterThanOrEq(startDate.getTime());
		
		AggregationPipeline pipeline = getDatastore().createAggregation(Event.class)
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
		
		Long numberOfDays = ChronoUnit.DAYS.between(LocalDate.of(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE)),
				LocalDate.of(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE)));
		
		AtomicReference<LocalDate> start = new AtomicReference<LocalDate>(LocalDate.now().minusDays(8));
		
		Map<String,AggregationResult> buckets = new HashMap<String,AggregationResult>();
		
		IntStream.range(0, numberOfDays.intValue()).forEach( i -> {
			String key = String.valueOf(start.get().getYear())
					.concat(String.valueOf(start.get().getMonthValue()))
					.concat(String.valueOf(start.get().getDayOfMonth()));
					
			buckets.put(key, new AggregationResult(String.valueOf(i), new Long(0)));
			
			start.set(start.get().plusDays(1));
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