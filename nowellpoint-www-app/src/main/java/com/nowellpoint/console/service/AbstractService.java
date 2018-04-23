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
	}
}