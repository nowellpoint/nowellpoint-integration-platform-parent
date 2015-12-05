package com.nowellpoint.aws.app.data;

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
		
	}

	@Override
	public Class<IsoCountry> getEncoderClass() {
		return IsoCountry.class;
	}

	@Override
	public IsoCountry decode(BsonReader reader, DecoderContext decoderContext) {
		reader.readStartDocument();
		
		IsoCountry isoCountry = new IsoCountry().id(reader.readObjectId("_id").toString())
				.code(reader.readString("code"))
				.name(reader.readString("name"))
				.description(reader.readString("description"));
		
		reader.readEndDocument();
		
		return isoCountry;
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