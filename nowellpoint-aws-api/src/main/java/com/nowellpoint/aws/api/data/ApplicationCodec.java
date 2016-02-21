package com.nowellpoint.aws.api.data;

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
import com.nowellpoint.aws.model.data.Application;

public class ApplicationCodec implements CollectibleCodec<Application> {
	
	private ObjectMapper objectMapper;
	private Codec<Document> documentCodec;

	public ApplicationCodec() {
		this.objectMapper = new ObjectMapper();
		this.documentCodec = new DocumentCodec();
	}

	@Override
	public void encode(BsonWriter writer, Application value, EncoderContext encoderContext) {
		
		Document document = null;
		try {
			document = Document.parse(objectMapper.writeValueAsString(value));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return;
		}
		
		documentCodec.encode(writer, document, encoderContext);
	}

	@Override
	public Class<Application> getEncoderClass() {
		return Application.class;
	}

	@Override
	public Application decode(BsonReader reader, DecoderContext decoderContext) {
		
		Document document = new DocumentCodec().decode(reader, decoderContext);
		
		Application entity = null;
		try {
			entity = objectMapper.readValue(document.toJson(), Application.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return entity;
	}

	@Override
	public boolean documentHasId(Application document) {
		return document.getId() != null;
	}

	@Override
	public Application generateIdIfAbsentFromDocument(Application document) {
		if (! documentHasId(document)) {
			document.setId(new ObjectId());
	    }
		return document;
	}

	@Override
	public BsonValue getDocumentId(Application document) {
		if (! documentHasId(document)) {
	        throw new IllegalStateException("The document does not contain an _id");
	    }	 
	    return new BsonString(document.getId().toString());
	}
}