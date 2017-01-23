package com.nowellpoint.mongodb.document;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;

public class DocumentManagerImpl extends AbstractDocumentManager implements DocumentManager {
	
	public DocumentManagerImpl(DocumentManagerFactory documentManagerFactory) {
		super(documentManagerFactory);
	}
	
	@Override
	public <T> String resolveCollectionName(Class<T> documentClass) {
		return resolveCollectionName(documentClass);
	}
	
	@Override
	public <T> Set<T> findAll(Class<T> documentClass) {
		
		final AtomicReference<Set<T>> documents = new AtomicReference<>();
		final AtomicReference<Throwable> throwable = new AtomicReference<>();
		final CountDownLatch latch = new CountDownLatch(1);
		
		SingleResultCallback<Void> callback = (Void, t) -> {
			if (t != null) {
	    		throwable.set(t);
	    	} 
			latch.countDown();
		};
		
		documents.set(new HashSet<>());
		
		Block<T> block = new Block<T>() {
		    @Override
		    public void apply(final T document) {
		        documents.get().add(document);
		    }
		};
		
		getCollection( documentClass ).withDocumentClass( documentClass ).find().forEach(block, callback);
		
		return documents.get();
	}
	
	@Override
	public <T> T findOne(Class<T> documentClass, ObjectId id) {
		return findOne(documentClass, Filters.eq ( "_id", id ) );
	}
	
	@Override
	public <T> T findOne(Class<T> documentClass, Bson query) {
		
		final AtomicReference<T> document = new AtomicReference<>();
		final AtomicReference<Throwable> throwable = new AtomicReference<>();
		final CountDownLatch latch = new CountDownLatch(1);
		
		SingleResultCallback<T> callback = (result, t) -> {
			if (t != null) {
	    		throwable.set(t);
	    	} else {
	    		document.set(result);
	    	}
			latch.countDown();
		};
		
		getCollection( documentClass ).withDocumentClass( documentClass ).find( query ).first( callback );
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		if (throwable.get() != null) {
			throw new RuntimeException(throwable.get());
		}
		
		if (document.get() == null) {
			throw new DocumentNotFoundException(String.format( "Document of type: %s was not found: %s", documentClass.getSimpleName(), bsonToString(query) ) );
		}
		
		return document.get();
	}
	
	@Override
	public <T> Set<T> find(Class<T> documentClass, Bson query) {
		
		final AtomicReference<Set<T>> documents = new AtomicReference<>();
		final AtomicReference<Throwable> throwable = new AtomicReference<>();
		final CountDownLatch latch = new CountDownLatch(1);
		
		SingleResultCallback<Void> callback = (Void, t) -> {
			if (t != null) {
	    		throwable.set(t);
	    	} 
			latch.countDown();
		};
		
		documents.set(new HashSet<>());
		
		Block<T> block = new Block<T>() {
		    @Override
		    public void apply(final T document) {
		        documents.get().add(document);
		    }
		};
		
		getCollection( documentClass ).withDocumentClass( documentClass ).find( query ).forEach(block, callback);
		
		return documents.get();
	}

	@Override
	public <T> void insertOne(T document) {
		
		setIdValue(document, new ObjectId());
		
		SingleResultCallback<Void> callback = (result, t) -> {
			if (t != null) {
				publish(t);
	    	}
		};
		
		@SuppressWarnings("unchecked")
		MongoCollection<T> collection = (MongoCollection<T>) getCollection( document.getClass() );
        collection.insertOne(document, callback);
	}
	
	@Override
	public <T> void upsert(Bson query, T document) {
		
		SingleResultCallback<UpdateResult> callback = (result, t) -> {
			if (t != null) {
				publish(t);
	    	}
		};
		
		@SuppressWarnings("unchecked")
		MongoCollection<T> collection = (MongoCollection<T>) getCollection( document.getClass() );
		collection.replaceOne(query, document, new UpdateOptions().upsert(true), callback);
	}
	
	@Override
	public <T> void replaceOne(T document) {
		
		Object id = resolveId(document);
		
		SingleResultCallback<UpdateResult> callback = (result, t) -> {
			if (t != null) {
	    		//throwable.set(t);
	    	} else {
	    		//document.set(result);
	    	}
		};
		
		@SuppressWarnings("unchecked")
		MongoCollection<T> collection = (MongoCollection<T>) getCollection( document.getClass() );
		collection.replaceOne( Filters.eq ( "_id", id ), document, callback );
	}
	
	@Override
	public <T> void deleteOne(T document) {
		
		Object id = resolveId(document);
		
		SingleResultCallback<DeleteResult> callback = (result, t) -> {
			if (t != null) {
	    		//throwable.set(t);
	    	} else {
	    		//document.set(result);
	    	}
		};
		
		@SuppressWarnings("unchecked")
		MongoCollection<T> collection = (MongoCollection<T>) getCollection( document.getClass() );
		collection.deleteOne(  Filters.eq ( "_id", id ), callback );
	}
	
	@Override
	public <T> void deleteMany(Class<T> documentClass, Bson query) {
		
		SingleResultCallback<DeleteResult> callback = (result, t) -> {
			if (t != null) {
	    		//throwable.set(t);
	    	} else {
	    		//document.set(result);
	    	}
		};
		
		MongoCollection<T> collection = (MongoCollection<T>) getCollection( documentClass );
		collection.deleteMany( query, callback );
	}
}