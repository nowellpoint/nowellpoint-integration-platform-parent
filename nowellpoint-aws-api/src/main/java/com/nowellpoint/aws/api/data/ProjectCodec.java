package com.nowellpoint.aws.api.data;

import java.io.IOException;
import java.util.UUID;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.model.data.Project;

public class ProjectCodec implements CollectibleCodec<Project> {
	
	private ObjectMapper objectMapper;
	private Codec<Document> documentCodec;

	public ProjectCodec() {
		this.objectMapper = new ObjectMapper();
		this.documentCodec = new DocumentCodec();
	}

	@Override
	public void encode(BsonWriter writer, Project value, EncoderContext encoderContext) {
		
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
	public Class<Project> getEncoderClass() {
		return Project.class;
	}

	@Override
	public Project decode(BsonReader reader, DecoderContext decoderContext) {
		
		Document document = new DocumentCodec().decode(reader, decoderContext);
		
		Project project = null;
		try {
			project = objectMapper.readValue(document.toJson(), Project.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return project;
	}

	@Override
	public boolean documentHasId(Project document) {
		return document.getId() != null;
	}

	@Override
	public Project generateIdIfAbsentFromDocument(Project document) {
		if (! documentHasId(document)) {
			document.setId(UUID.randomUUID().toString());
	    }
		return document;
	}

	@Override
	public BsonValue getDocumentId(Project document) {
		if (! documentHasId(document)) {
	        throw new IllegalStateException("The document does not contain an _id");
	    }	 
	    return new BsonString(document.getId());
	}
}