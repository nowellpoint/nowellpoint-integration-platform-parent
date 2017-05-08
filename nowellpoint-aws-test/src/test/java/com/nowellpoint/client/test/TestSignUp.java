package com.nowellpoint.client.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.GetPlansRequest;
import com.nowellpoint.client.model.Plan;
import com.nowellpoint.util.Properties;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;

public class TestSignUp {
	
	private static Logger log = Logger.getLogger(TestSignUp.class);
	
	private static MongoClientURI mongoClientURI;
	private static MongoClient mongoClient;
	private static MongoDatabase mongoDatabase;
	private static BraintreeGateway gateway;
	private static ApiKey apiKey;
	private static Client client;
	
	@BeforeClass
	public static void beforeClass() {
		Properties.loadProperties(System.getenv("NOWELLPOINT_PROPERTY_STORE"));
		
		mongoClientURI = new MongoClientURI("mongodb://".concat(System.getProperty(Properties.MONGO_CLIENT_URI)));
		mongoClient = new MongoClient(mongoClientURI);	
		mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
		
		gateway = new BraintreeGateway(
				Environment.parseEnvironment(System.getProperty(Properties.BRAINTREE_ENVIRONMENT)),
				System.getProperty(Properties.BRAINTREE_MERCHANT_ID),
				System.getProperty(Properties.BRAINTREE_PUBLIC_KEY),
				System.getProperty(Properties.BRAINTREE_PRIVATE_KEY)
		);
		
		gateway.clientToken().generate();
		
		apiKey = ApiKeys.builder()
				.setId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.setSecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.build();
		
		client = Clients.builder()
				.setApiKey(apiKey)
				.build();
	}
	
	@AfterClass
	public static void afterClass() {
		mongoClient.close();
	}

	@Test
	public void testSignUp() {
		
//		GetPlansRequest getPlansRequest = new GetPlansRequest()
//				.withLanguage("en_US")
//				.withLocale("en_US");
//		
//		List<Plan> plans = NowellpointClient.defaultClient(token).plan()
//				.getPlans(getPlansRequest)
//				.getItems();
//		
//		assertNotNull(plans);
//		
//		plans = plans.stream()
//				.sorted((p1, p2) -> p1.getPrice().getUnitPrice().compareTo(p2.getPrice().getUnitPrice()))
//				.collect(Collectors.toList());
//		
//		plans.stream().forEach(plan -> {
			
			//log.info("testing plan: " + plan.getPlanName());
			
//			SignUpRequest signUpRequest = new SignUpRequest()
//					.withFirstName("Test")
//					.withLastName("User")
//					.withEmail("test.nowellpoint@mailinator.com")
//					.withPassword("!t2U1&JUTJvY")
//					.withConfirmPassword("!t2U1&JUTJvY")
//					.withCountryCode("US")
//					.withPlanId(plan.getId())
//					.withCardNumber("4111111111111111")
//					.withExpirationMonth("12")
//					.withExpirationYear("2018")
//					.withSecurityCode("123");
//			
//			SignUpResult<User> signUpResult = new NowellpointClientOrig()
//					.user()
//					.signUp(signUpRequest);
//			
//			String accountProfileId = signUpResult.getTarget().getHref().substring(signUpResult.getTarget().getHref().lastIndexOf("/") + 1);
//			
//			assertTrue(signUpResult.isSuccess());
//			assertNotNull(signUpResult.getTarget());
//			assertNotNull(signUpResult.getTarget().getHref());
//			assertNotNull(signUpResult.getTarget().getEmailVerificationToken());
//			
//			signUpResult = new NowellpointClientOrig()
//					.user()
//					.verifyEmail(signUpResult.getTarget().getEmailVerificationToken());
//			
//			assertTrue(signUpResult.isSuccess());
//			
//			Document document = mongoDatabase.getCollection("account.profiles")
//					.find(Filters.eq ( "_id", new ObjectId( accountProfileId ) ) )
//					.first();
//			
//			assertNotNull(document.getString("accountHref"));
//			assertNull(document.getString("emailVerificationToken"));
//			assertTrue(document.getBoolean("isActive"));
//			
//			System.out.println(document.getString("accountHref"));
//			
//			mongoDatabase.getCollection("account.profiles").deleteOne( Filters.eq ( "_id", new ObjectId( accountProfileId ) ) );
//			
//			gateway.customer().delete(accountProfileId);
//			
//			client.getResource(document.getString("accountHref"), Account.class).delete();
			
//		});
	}
}