package com.nowellpoint.mongodb;

import java.io.Serializable;
import java.util.List;

import org.bson.codecs.Codec;

import com.mongodb.ConnectionString;
import com.nowellpoint.mongodb.document.DocumentManagerFactoryImpl;
import com.nowellpoint.util.Properties;

public class Datastore implements Serializable {
	
	private static final long serialVersionUID = 4959176736907644065L;
	
	private static DocumentManagerFactory documentManagerFactory;

	private Datastore() {
		
	}
	
	public static DocumentManagerFactory createDocumentManagerFactory() {
		ConnectionString connectionString = new ConnectionString("mongodb://".concat(System.getProperty(Properties.MONGO_CLIENT_URI)));
		documentManagerFactory = new DocumentManagerFactoryImpl(connectionString);
		return documentManagerFactory;
	}
	
	public static DocumentManagerFactory createDocumentManagerFactory(String mongoClientUri) {
		ConnectionString connectionString = new ConnectionString( mongoClientUri.startsWith("mongodb://") ?  mongoClientUri : "mongodb://".concat(mongoClientUri) );
		documentManagerFactory = new DocumentManagerFactoryImpl(connectionString);
		return documentManagerFactory;
	}
	
	public static DocumentManagerFactory createDocumentManagerFactory(List<Codec<?>> codecs) {
		ConnectionString connectionString = new ConnectionString("mongodb://".concat(System.getProperty(Properties.MONGO_CLIENT_URI)));
		documentManagerFactory = new DocumentManagerFactoryImpl(connectionString, codecs);
		return documentManagerFactory;
	}
	
	public static DocumentManagerFactory getCurrentSession() {
		return documentManagerFactory;
	}
}