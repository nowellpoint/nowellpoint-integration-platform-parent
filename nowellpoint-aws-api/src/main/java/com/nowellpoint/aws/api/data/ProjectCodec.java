package com.nowellpoint.aws.api.data;

import java.util.Date;
import java.util.UUID;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import com.nowellpoint.aws.model.data.Project;

public class ProjectCodec implements CollectibleCodec<Project> {

	
	public ProjectCodec() {
		
	}

	@Override
	public void encode(BsonWriter writer, Project value, EncoderContext encoderContext) {
		writer.writeStartDocument();
		writer.writeString("_id", value.getId());
		writer.writeString("name", value.getName());
		writer.writeString("description", value.getDescription());
		writer.writeString("stage", value.getStage());
		writer.writeDateTime("creationDate", value.getCreationDate().getTime());
		writer.writeDateTime("lastModifiedDate", value.getLastModifiedDate().getTime());
		writer.writeString("owner", value.getOwner());
		writer.writeString("createdBy", value.getCreatedBy());
		writer.writeString("lastModifiedBy", value.getLastModifiedBy());
		writer.writeEndDocument();
	}

	@Override
	public Class<Project> getEncoderClass() {
		return Project.class;
	}

	@Override
	public Project decode(BsonReader reader, DecoderContext decoderContext) {
		reader.readStartDocument();
		String id = reader.readString("_id");
		String name = reader.readString("name");
		String description = reader.readString("description");
		String stage = reader.readString("stage");
		Long creationDate = reader.readDateTime("creationDate");
		Long lastModifiedDate = reader.readDateTime("lastModifiedDate");
		String owner = reader.readString("owner");
		String createdBy = reader.readString("createdBy");
		String lastModifiedBy = reader.readString("lastModifiedBy");
		reader.readEndDocument();
		
		return new Project().id(id)
				.name(name)
				.description(description)
				.stage(stage)
				.creationDate(new Date(creationDate))
				.lastModifiedDate(new Date(lastModifiedDate))
				.owner(owner)
				.createdBy(createdBy)
				.lastModifiedBy(lastModifiedBy);
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