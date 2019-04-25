package com.nowellpoint.console.mongodb;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.FindOptions;

import com.nowellpoint.client.sforce.model.User;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.console.entity.Organization;
import com.nowellpoint.console.entity.StreamingEvent;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.util.SecretsManager;

public class TestPlaybook {
	
private static final Logger logger = Logger.getLogger(TestGroupBy.class.getName());
	
	private static final String ORGANIZATION_ID = "5bac3c0e0626b951816064f5";
	
	private static MongoClient mongoClient;
	private static Datastore datastore;
	
	@BeforeClass
	public static void start() {
		MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", SecretsManager.getMongoClientUri()));
		mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();
        morphia.map(Organization.class);
        morphia.map(StreamingEvent.class);

        datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
        datastore.ensureIndexes();
	}

	@Test
	public void testExecutePlaybook() {
		
		Organization organization = datastore.get(Organization.class, new ObjectId(ORGANIZATION_ID));
		
		logger.info(organization.getName());
		
		FindOptions options = new FindOptions().limit(1);
		
		List<StreamingEvent> streamingEvents = datastore.createQuery(StreamingEvent.class).asList(options);
		
		Token token = ServiceClient.getInstance()
				.salesforce()
				.refreshToken(organization.getConnection().getRefreshToken());
		
		String query = "Select Id, FirstName, LastName, Name From User Where Id = '%s' or Id = '%s'";
		
		streamingEvents.stream().forEach(s -> {
			Set<User> users = SalesforceClientBuilder.defaultClient(token).query(User.class, String.format(query,s.getPayload().getCreatedById(), s.getPayload().getLastModifiedById()));
			users.stream().forEach(u -> {
				if (u.getId().equals(s.getPayload().getCreatedById())) {
					logger.info("Created By: " + u.getName());
				} 
				if (u.getId().equals(s.getPayload().getLastModifiedById())) {
					logger.info("Last Modified By: " + u.getName());
				}
			});
		});
		
		long start = System.currentTimeMillis();
		
		ZoneId zoneId = ZoneId.of("UTC");
		
		LocalDate today = LocalDate.now( zoneId );
		LocalDate firstDayOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
		LocalDate firstDayOfMonth = LocalDate.of(today.getYear(), today.getMonth(), 1);
		LocalDate firstDayOfYear = LocalDate.of(today.getYear(), 1, 1);
		
		ExecutorService executor = Executors.newFixedThreadPool(4);
		
		Future<Long> todayTask = executor.submit(() -> {
			return countEvents(organization.getId(), today, zoneId);
        });
		
		Future<Long> thisWeekTask = executor.submit(() -> {
			return countEvents(organization.getId(), firstDayOfWeek, zoneId);
        });
		
		Future<Long> thisMonthTask = executor.submit(() -> {
			return countEvents(organization.getId(), firstDayOfMonth, zoneId);
        });
		
		Future<Long> thisYearTask = executor.submit(() -> {
			return countEvents(organization.getId(), firstDayOfYear, zoneId);
        });
		
		try {
			logger.info(todayTask.get());
			logger.info(thisWeekTask.get());
			logger.info(thisMonthTask.get());
			logger.info(thisYearTask.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}
				
		logger.info(System.currentTimeMillis() - start);
	}
	
	@AfterClass 
	public static void stop() {
		mongoClient.close();
	}
	
	private Long countEvents(ObjectId organizationId, LocalDate from, ZoneId zoneId) {
		return datastore.createQuery(StreamingEvent.class)
				.field("organizationId")
				.equal(organizationId)
				.field("eventDate")
				.greaterThanOrEq(Date.from(from.atStartOfDay()
						.atZone(zoneId)
						.toInstant()))
				.count();
	}
}