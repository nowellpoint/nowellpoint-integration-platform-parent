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
import com.nowellpoint.aws.model.data.Identity;

public class IdentityCodec implements CollectibleCodec<Identity> {
	
	private ObjectMapper objectMapper;
	private Codec<Document> documentCodec;
	
	public IdentityCodec() {
		this.objectMapper = new ObjectMapper();
		this.documentCodec = new DocumentCodec();
	}

	@Override
	public void encode(BsonWriter writer, Identity value, EncoderContext encoderContext) {
		
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
	public Class<Identity> getEncoderClass() {
		return Identity.class;
	}

	@Override
	public Identity decode(BsonReader reader, DecoderContext decoderContext) {
		
		Document document = new DocumentCodec().decode(reader, decoderContext);
		
		Identity identity = null;
		try {
			identity = objectMapper.readValue(document.toJson(), Identity.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return identity;
	}

	@Override
	public boolean documentHasId(Identity document) {
		return document.getId() != null;
	}

	@Override
	public Identity generateIdIfAbsentFromDocument(Identity document) {
		if (! documentHasId(document)) {
			document.setId(new ObjectId());
	    }
		return document;
	}

	@Override
	public BsonValue getDocumentId(Identity document) {
		if (! documentHasId(document)) {
	        throw new IllegalStateException("The document does not contain an _id");
	    }	 
	    return new BsonString(document.getId().toString());
	}
}