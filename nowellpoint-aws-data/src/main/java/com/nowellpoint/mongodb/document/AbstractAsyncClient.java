package com.nowellpoint.mongodb.document;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public abstract class AbstractAsyncClient {
	
	public Document findOne(MongoCollection<Document> collection, Bson query) {
		
		final AtomicReference<Document> document = new AtomicReference<>();
		final AtomicReference<Throwable> throwable = new AtomicReference<>();
		final CountDownLatch latch = new CountDownLatch(1);
		
		SingleResultCallback<Document> callback = (result, t) -> {
			if (t != null) {
	    		throwable.set(t);
	    	} else {
	    		document.set(result);
	    	}
			latch.countDown();
		};
		
		collection.find( query ).first( callback );
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		if (throwable.get() != null) {
			throw new RuntimeException(throwable.get());
		}
		
		return document.get();
	}
	
	public Set<Document> find(MongoCollection<Document> collection, Bson query) {
		
		final AtomicReference<Set<Document>> documents = new AtomicReference<>();
		final AtomicReference<Throwable> throwable = new AtomicReference<>();
		final CountDownLatch latch = new CountDownLatch(1);
		
		SingleResultCallback<Void> callback = (Void, t) -> {
			if (t != null) {
	    		throwable.set(t);
	    	} 
			latch.countDown();
		};
		
		documents.set(new HashSet<>());
		
		Block<Document> block = (document) -> {
			documents.get().add(document);
		};
		
		collection.find( query ).forEach(block, callback);
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		if (throwable.get() != null) {
			throw new RuntimeException(throwable.get());
		}
		
		return documents.get();
	}
	
	public Set<Document> findAll(MongoCollection<Document> collection) {
		
		final AtomicReference<Set<Document>> documents = new AtomicReference<>();
		final AtomicReference<Throwable> throwable = new AtomicReference<>();
		final CountDownLatch latch = new CountDownLatch(1);
		
		SingleResultCallback<Void> callback = (Void, t) -> {
			if (t != null) {
	    		throwable.set(t);
	    	} 
			latch.countDown();
		};
		
		documents.set(new HashSet<>());
		
		Block<Document> block = (document) -> {
			documents.get().add(document);
		};
		
		collection.find().forEach(block, callback);
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		if (throwable.get() != null) {
			throw new RuntimeException(throwable.get());
		}
		
		return documents.get();
	}
	
	public void insertOne(MongoCollection<Document> collection, Document document) {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> throwable = new AtomicReference<>();
		
		SingleResultCallback<Void> callback = (result, t) -> {
			if (t != null) {
				throwable.set(t);
	    	}
			latch.countDown();
		};
		
		collection.insertOne(document, callback);
        
        try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
        
        if (throwable.get() != null) {
			throw new RuntimeException(throwable.get());
		}
	}
	
	public void replaceOne(MongoCollection<Document> collection, Document document, Bson query) {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> throwable = new AtomicReference<>();
		
		SingleResultCallback<UpdateResult> callback = (result, t) -> {
			if (t != null) {
				throwable.set(t);
	    	}
			latch.countDown();
		};
		
		collection.replaceOne( query, document, callback );
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
        
        if (throwable.get() != null) {
			throw new RuntimeException(throwable.get());
		}
	}
	
	public void upsert(MongoCollection<Document> collection, Document document, Bson query) {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> throwable = new AtomicReference<>();
		
		SingleResultCallback<UpdateResult> callback = (result, t) -> {
			if (t != null) {
				throwable.set(t);
	    	}
			latch.countDown();
		};
		
		collection.replaceOne(query, document, new UpdateOptions().upsert(true), callback);
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
        
        if (throwable.get() != null) {
			throw new RuntimeException(throwable.get());
		}
	}
	
	public void deleteOne(MongoCollection<Document> collection, Bson query) {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> throwable = new AtomicReference<>();
		
		SingleResultCallback<DeleteResult> callback = (result, t) -> {
			if (t != null) {
				throwable.set(t);
	    	}
			latch.countDown();
		};
		
		collection.deleteOne(  query, callback );
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
        
        if (throwable.get() != null) {
			throw new RuntimeException(throwable.get());
		}
	}
	
	public void deleteMany(MongoCollection<Document> collection, Bson query) {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> throwable = new AtomicReference<>();
		
		SingleResultCallback<DeleteResult> callback = (result, t) -> {
			if (t != null) {
				throwable.set(t);
	    	} else {
	    		System.out.println("deleted: " + result.getDeletedCount());
	    	}
			latch.countDown();
		};
		
		collection.deleteMany(  query, callback );
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
        
        if (throwable.get() != null) {
			throw new RuntimeException(throwable.get());
		}
	}

}
