package com.nowellpoint.console.mongodb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.console.entity.AggregationResult;
import com.nowellpoint.console.entity.Organization;
import com.nowellpoint.console.entity.OrganizationDAO;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.util.SecretsManager;

public class TestGroupBy {
	
	private static final Logger logger = Logger.getLogger(TestGroupBy.class.getName());
	
	private static final String ORGANIZATION_ID = "5bac3c0e0626b951816064f5";
	
	private static MongoClient mongoClient;
	private static Datastore datastore;
	
	@BeforeClass
	public static void start() {
		MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", SecretsManager.getMongoClientUri()));
		mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();

        datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
        datastore.ensureIndexes();
	}
	
	@Test
	public void testStreamingEventStatistics() {
		
		Organization organization = datastore.get(Organization.class, new ObjectId(ORGANIZATION_ID));
		
		LocalDate today = LocalDate.now( ZoneId.of( "UTC" ) );
		LocalDate firstDayOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
		LocalDate firstDayOfMonth = LocalDate.of(today.getYear(), today.getMonth(), 1);
		LocalDate firstDayOfYear = LocalDate.of(today.getYear(), 1, 1);
		Long daysBetween = ChronoUnit.DAYS.between(today.minusYears(1).plusDays(1), today);
		
		List<AggregationResult> results = ServiceClient.getInstance()
				.organization()
				.getEventsBySourceByDays(organization.getId().toString(), "Account", daysBetween.intValue());
		
		AtomicLong eventsToday = new AtomicLong(0);
		AtomicLong eventsThisWeek = new AtomicLong(0);
		AtomicLong eventsThisMonth = new AtomicLong(0);
		AtomicLong eventsThisYear = new AtomicLong(0);
		
		results.forEach(r -> {
			if (r.getGroupByDate().isEqual(today)) {
				eventsToday.set(r.getCount());
			} 
			if (r.getGroupByDate().isEqual(firstDayOfWeek) || r.getGroupByDate().isAfter(firstDayOfWeek)) {
				eventsThisWeek.addAndGet(r.getCount());
			} 
			if (r.getGroupByDate().isEqual(firstDayOfMonth) || r.getGroupByDate().isAfter(firstDayOfMonth)) {
				eventsThisMonth.addAndGet(r.getCount());
			}
			if (r.getGroupByDate().isEqual(firstDayOfYear) || r.getGroupByDate().isAfter(firstDayOfYear)) {
				eventsThisYear.addAndGet(r.getCount());
			}
		});
		
		System.out.println("Today: " + eventsToday.get());
		System.out.println("This week: " + eventsThisWeek.get());
		System.out.println("This month: " + eventsThisMonth.get());
		System.out.println("This year: " + eventsThisYear.get());
	}
	
	@Test
	public void testSalesforceIdentity() {

		Organization organization = datastore.get(Organization.class, new ObjectId(ORGANIZATION_ID));
		
		assertNotNull(organization.getName());
		assertNotNull(organization.getNumber());
		
		System.out.println(organization.getConnection().getRefreshToken());
		
		Token token = ServiceClient.getInstance()
				.salesforce()
				.refreshToken(organization.getConnection().getRefreshToken());
		
		long start = System.currentTimeMillis();
		
		ServiceClient.getInstance().salesforce().getIdentity(token);
		
		logger.info("getIdentity execution time: " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		
		Identity identity = ServiceClient.getInstance()
				.salesforce()
				.getIdentity(token);
		
		assertTrue((System.currentTimeMillis() - start) < 3);
		assertNotNull(identity);
		assertNotNull(identity.getActive());
		assertNotNull(identity.getCity());
		assertNotNull(identity.getCountry());
		assertNotNull(identity.getState());
		assertNotNull(identity.getStreet());
		assertNotNull(identity.getPostalCode());
		assertNotNull(identity.getAssertedUser());
		assertNotNull(identity.getDisplayName());
		assertNotNull(identity.getEmail());
		assertNotNull(identity.getFirstName());
		assertNotNull(identity.getLastName());
		assertNotNull(identity.getId());
		assertNotNull(identity.getLanguage());
		assertNotNull(identity.getLocale());
		
		logger.info(identity.getDisplayName());
	}
	
	@Test
	public void testGroupBy() throws IOException {
		
		OrganizationDAO dao = new OrganizationDAO(Organization.class, datastore);
		
		List<AggregationResult> results = dao.getEventsLastDays(new ObjectId(ORGANIZATION_ID), 7);
		
		String data = results.stream()
				.sorted(Comparator.reverseOrder())
				.map(r -> formatLabel(Locale.getDefault(), r))
				.collect(Collectors.joining(", "));
		
		System.out.println(data);
	}
	
	@AfterClass
	public static void stop() {
		mongoClient.close();
	}
	
	private static String formatLabel(Locale locale, AggregationResult result) {
		
		ZoneId utc = ZoneId.of( "UTC" );
		
		LocalDate now = LocalDate.now( utc ).minusDays(Integer.valueOf(result.getId()));
		
		String text = null;
		if (now.equals(LocalDate.now( utc ))) {
			text = "Today";
		} else if (now.equals(LocalDate.now( utc ).minusDays(1))) {
			text = "Yesterday";
		} else {
			text = now.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
		}
		
		return new StringBuilder("['")
				.append(text)
				.append("'")
				.append(", ")
				.append(result.getCount())
				.append("]")
				.toString();
	}
}