package com.nowellpoint.console.entity;

import java.util.Iterator;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.aggregation.Accumulator;
import org.mongodb.morphia.aggregation.AggregationPipeline;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.MorphiaIterator;
import org.mongodb.morphia.aggregation.Group;
import static org.mongodb.morphia.aggregation.Group.id;
import static org.mongodb.morphia.aggregation.Group.grouping;

import com.mongodb.AggregationOptions;

public class EventDAO extends BasicDAO<Event,String> {

	public EventDAO(Class<Event> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
	
	public void getEventsLast7Days() {
		AggregationOptions options = AggregationOptions.builder()
                .outputMode(AggregationOptions.OutputMode.CURSOR)
                .build();
		
		AggregationPipeline aggregate = getDatastore().createAggregation(Event.class).group(
				id(
                        grouping("year", new Accumulator("$year", "startTime")),
                        grouping("month", new Accumulator("$month", "startTime")),
                        grouping("day", new Accumulator("$dayOfMonth", "startTime"))
                    )
				);
		
		

	}
}