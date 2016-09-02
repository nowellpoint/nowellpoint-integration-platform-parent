package com.nowellpoint.aws.data;

import java.io.IOException;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.data.mongodb.MongoDocument;

public class AbstractCodec<T extends MongoDocument> implements CollectibleCodec<T> {
	
	private final ObjectMapper objectMapper;
	private final Codec<Document> documentCodec;
	private final Class<T> type;
	
	public AbstractCodec(Class<T> type) {
		this.objectMapper = new ObjectMapper();
		this.documentCodec = new DocumentCodec();
		this.type = type;
	}

	@Override
	public void encode(BsonWriter writer, T value, EncoderContext context) {
		Document document = null;
		try {
			document = Document.parse(objectMapper.writeValueAsString(value));
			document.put("_id", value.getId());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return;
		}
		
		documentCodec.encode(writer, document, context);
		
	}

	@Override
	public Class<T> getEncoderClass() {
		return type;
	}

	@Override
	public T decode(BsonReader reader, DecoderContext context) {
		Document document = documentCodec.decode(reader, context);

		T object = null;
		try {
			object = objectMapper.readValue(document.toJson(), type);
			object.setId(document.getObjectId("_id"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return object;
	}

	@Override
	public boolean documentHasId(T document) {
		return document.getId() != null;
	}

	@Override
	public T generateIdIfAbsentFromDocument(T document) {
		if (! documentHasId(document)) {
			document.setId(new ObjectId());
	    }
		return document;
	}

	@Override
	public BsonValue getDocumentId(T document) {
		if (! documentHasId(document)) {
	        throw new IllegalStateException("The document does not contain an _id");
	    }	 
	    return new BsonString(document.getId().toString());
	}
}