package com.nowellpoint.aws.api.data;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import com.nowellpoint.aws.model.IsoCountry;

public class IsoCountryCodec implements CollectibleCodec<IsoCountry> {
	
	public IsoCountryCodec() {
		
	}

	@Override
	public void encode(BsonWriter writer, IsoCountry value, EncoderContext encoderContext) {
		writer.writeStartDocument();
		writer.writeObjectId("_id", new ObjectId(value.getId()));
		writer.writeString("language", value.getLanguage());
		writer.writeString("code", value.getCode());
		writer.writeString("name", value.getName());
		writer.writeString("description", value.getDescription());
		writer.writeEndDocument();
	}

	@Override
	public Class<IsoCountry> getEncoderClass() {
		return IsoCountry.class;
	}

	@Override
	public IsoCountry decode(BsonReader reader, DecoderContext decoderContext) {
		reader.readStartDocument();
		String id = reader.readObjectId("_id").toString();
		String language = reader.readString("language");
		String code = reader.readString("code");
		String name = reader.readString("name");
		String description = reader.readString("description");		
		reader.readEndDocument();
		
		return new IsoCountry().id(id)
				.language(language)
				.code(code)
				.name(name)
				.description(description);
	}

	@Override
	public boolean documentHasId(IsoCountry isoCountry) {
		return isoCountry.getId() != null;
	}

	@Override
	public IsoCountry generateIdIfAbsentFromDocument(IsoCountry isoCountry) {
		if (! documentHasId(isoCountry)) {
			isoCountry.setId(new ObjectId().toString());
	    }
		return isoCountry;
	}

	@Override
	public BsonValue getDocumentId(IsoCountry isoCountry) {
		if (! documentHasId(isoCountry)) {
	        throw new IllegalStateException("The document does not contain an _id");
	    }	 
	    return new BsonString(isoCountry.getId());
	}
}