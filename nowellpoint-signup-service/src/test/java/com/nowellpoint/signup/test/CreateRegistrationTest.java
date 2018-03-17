package com.nowellpoint.signup.test;

import java.sql.Date;
import java.time.Instant;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.signup.entity.RegistrationDAO;
import com.nowellpoint.signup.entity.RegistrationDocument;
import com.nowellpoint.signup.entity.UserProfile;
import com.nowellpoint.signup.model.ModifiableRegistration;
import com.nowellpoint.signup.model.ModifiableUserInfo;
import com.nowellpoint.signup.model.Registration;
import com.nowellpoint.signup.model.UserInfo;

public class CreateRegistrationTest {
	
	@Test
	public void testCreateRegistration() {
		
		MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", System.getenv("MONGO_CLIENT_URI")));
		
		MongoClient mongoClient = new MongoClient(mongoClientUri);
		
		final Morphia morphia = new Morphia();
		
		morphia.mapPackage("com.nowellpoint.signup.entity");
		
		final Datastore datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
		datastore.ensureIndexes();
		
		ModelMapper mapper = new ModelMapper();
		mapper.addConverter(new AbstractConverter<String, ObjectId>() {
			@Override
			protected ObjectId convert(String source) {
				return source == null ? null : new ObjectId(source);
			}
		});
		mapper.addConverter(new AbstractConverter<ObjectId, String>() {		
			@Override
			protected String convert(ObjectId source) {
				return source == null ? null : source.toString();
			}
		});
		mapper.addConverter(new AbstractConverter<UserProfile, UserInfo>() {
			@Override
			protected UserInfo convert(UserProfile source) {
				return source == null ? null : mapper.map(source, ModifiableUserInfo.class).toImmutable();
			}
		});
		
		Query<UserProfile> query = datastore
				.createQuery(UserProfile.class)
				.field("username")
				.equal("system.administrator@nowellpoint.com");
				 
		UserProfile userProfile = query.get();
		
		UserInfo userInfo = mapper.map(userProfile, ModifiableUserInfo.class).toImmutable();
		
		Registration registration = Registration.builder()
				.countryCode("US")
				.email("jherson@aim.com")
				.phone("999-999-9999")
				.emailVerificationToken("jfioufidfdf")
				.firstName("John")
				.lastName("Herson")
				.verified(Boolean.FALSE)
				.planId("9999")
				.createdBy(userInfo)
				.createdOn(Date.from(Instant.now()))
				.lastUpdatedBy(userInfo)
				.lastUpdatedOn(Date.from(Instant.now()))
				.domain("nowellpoint")
				.expiresAt(Instant.now().plusSeconds(1209600).toEpochMilli())
				.build();
		
		RegistrationDAO dao = new RegistrationDAO(RegistrationDocument.class, datastore);
		
		RegistrationDocument document = mapper.map(registration, RegistrationDocument.class);
		
		dao.save(document);
		
		document.setPhone(null);
		
		datastore.save(document);
		
		registration = mapper.map(document, ModifiableRegistration.class).toImmutable();
		
		System.out.println(registration.getId());
		System.out.println(registration.getCreatedBy().getId());
		System.out.println(registration.getCreatedBy().getName());
		System.out.println(registration.getLastUpdatedBy().getId());
		System.out.println(registration.getLastUpdatedBy().getName());
	}
}