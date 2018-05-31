package com.nowellpoint.console.service;

import org.bson.types.ObjectId;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.console.entity.Lead;
import com.nowellpoint.console.model.Address;
import com.nowellpoint.console.model.ModifiableAddress;
import com.nowellpoint.console.model.ModifiableOrganization;
import com.nowellpoint.console.model.ModifiablePhotos;
import com.nowellpoint.console.model.ModifiablePreferences;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.Photos;
import com.nowellpoint.console.model.Preferences;
import com.nowellpoint.www.app.util.EnvironmentVariables;

public abstract class AbstractService {
	
	protected static final Datastore datastore;
	
	static {
		final MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", EnvironmentVariables.getMongoClientUri()));
		MongoClient mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();
        
        morphia.map(Lead.class);

        datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
        datastore.ensureIndexes();
	}
	
	protected static final ModelMapper modelMapper;
	
	static {
		modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		modelMapper.getConfiguration().setMethodAccessLevel(AccessLevel.PRIVATE);
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
		modelMapper.getConfiguration().setFieldAccessLevel(AccessLevel.PRIVATE);
		modelMapper.addConverter(new AbstractConverter<String, ObjectId>() {
			@Override
			protected ObjectId convert(String source) {
				return source == null ? null : new ObjectId(source);
			}
		});
		modelMapper.addConverter(new AbstractConverter<ObjectId, String>() {		
			@Override
			protected String convert(ObjectId source) {
				return source == null ? null : source.toString();
			}
		});
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.console.entity.Address, Address>() {		
			@Override
			protected Address convert(com.nowellpoint.console.entity.Address source) {
				return source == null ? null : modelMapper.map(source, ModifiableAddress.class).toImmutable();
			}
		});
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.console.entity.Preferences, Preferences>() {		
			@Override
			protected Preferences convert(com.nowellpoint.console.entity.Preferences source) {
				return source == null ? null : modelMapper.map(source, ModifiablePreferences.class).toImmutable();
			}
		});
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.console.entity.Organization, Organization>() {		
			@Override
			protected Organization convert(com.nowellpoint.console.entity.Organization source) {
				return source == null ? null : modelMapper.map(source, ModifiableOrganization.class).toImmutable();
			}
		});
		modelMapper.addConverter(new AbstractConverter<com.nowellpoint.console.entity.Photos, Photos>() {		
			@Override
			protected Photos convert(com.nowellpoint.console.entity.Photos source) {
				return source == null ? null : modelMapper.map(source, ModifiablePhotos.class).toImmutable();
			}
		});
	}
}