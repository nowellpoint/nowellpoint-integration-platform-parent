package com.nowellpoint.signup.test;

import java.time.Instant;

import org.junit.Test;
import org.modelmapper.ModelMapper;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.signup.entity.RegistrationDAO;
import com.nowellpoint.signup.entity.RegistrationDocument;
import com.nowellpoint.signup.model.Registration;

public class CreateRegistrationTest {
	
	@Test
	public void testCreateRegistration() {
		Registration registration = Registration.builder()
				.countryCode("US")
				.email("jherson@aim.com")
				.phone("999-999-9999")
				.emailVerificationToken("jfioufidfdf")
				.firstName("John")
				.lastName("Herson")
				.verified(Boolean.FALSE)
				.planId("9999")
				//.createdBy(userInfo)
				//.lastUpdatedBy(userInfo)
				//.lastUpdatedOn(now)
				.domain("nowellpoint")
				.expiresAt(Instant.now().plusSeconds(1209600).toEpochMilli())
				.build();
		
		MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", System.getenv("MONGO_CLIENT_URI")));
		
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(mongoClientUri);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final Morphia morphia = new Morphia();
		
		morphia.mapPackage("com.nowellpoint.signup.entity");
		
		final Datastore datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
		datastore.ensureIndexes();
		
		RegistrationDAO dao = new RegistrationDAO(RegistrationDocument.class, datastore);
		
		ModelMapper mapper = new ModelMapper();
		
		RegistrationDocument document = mapper.map(registration, RegistrationDocument.class);
		
		dao.save(document);
		
		document.setPhone(null);
		
		datastore.save(document);
		
	}
}