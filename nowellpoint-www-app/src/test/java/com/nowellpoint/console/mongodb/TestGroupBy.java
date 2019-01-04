package com.nowellpoint.console.mongodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.Salesforce;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.console.entity.AggregationResult;
import com.nowellpoint.console.entity.Organization;
import com.nowellpoint.console.entity.OrganizationDAO;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.util.SecretsManager;

public class TestGroupBy {
	
	private static final Logger logger = Logger.getLogger(TestGroupBy.class.getName());
	private static ObjectMapper mapper = new ObjectMapper();
	
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
	public void testSalesforceIdentity() {

		Organization organization = datastore.get(Organization.class, new ObjectId("5bac3c0e0626b951816064f5"));
		
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
	public void testStreamingEventListener() {
		Organization organization = datastore.get(Organization.class, new ObjectId("5bac3c0e0626b951816064f5"));
		
		assertNotNull(organization.getName());
		assertNotNull(organization.getNumber());
		
		System.out.println(organization.getConnection().getRefreshToken());
		
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(SecretsManager.getSalesforceClientId())
				.setClientSecret(SecretsManager.getSalesforceClientSecret())
				.setRefreshToken(organization.getConnection().getRefreshToken())
				.build();
		
		OauthAuthenticationResponse oauthAthenticationResponse = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		Token token = oauthAthenticationResponse.getToken();
		
		Salesforce client = SalesforceClientBuilder.builder().build().getClient();
		
		Identity identity = client.getIdentity(token);
		
		int i = 0;
		
		while (i < 10) {
			updateAccount(token.getAccessToken(), identity.getUrls().getSObjects(), "0013A00001YjszLQAR");
			i++;
		}
	}
	
	@Test
	public void testGroupBy() throws IOException {
		
		OrganizationDAO dao = new OrganizationDAO(Organization.class, datastore);
		
		List<AggregationResult> results = dao.getEventsLastDays(new ObjectId("5bac3c0e0626b951816064f5"), 7);
		
		String data = results.stream()
				.sorted(Comparator.reverseOrder())
				.map(r -> formatLabel(Locale.getDefault(), r))
				.collect(Collectors.joining(", "));
		
		System.out.println(data);
	}
	
	private void updateAccount(String accessToken, String sobjectUrl, String accountId) {
		
		String body = mapper.createObjectNode()
				.put("Rating", "Hot")
				.toString();
		
		HttpResponse response = RestResource.post(sobjectUrl.concat("Account/").concat(accountId).concat("/?_HttpMethod=PATCH"))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(accessToken)
				.body(body)
				.contentType(MediaType.APPLICATION_JSON)
                .execute();
		
		assertEquals(204, response.getStatusCode());
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