package com.nowellpoint.listener.model;

import java.io.Serializable;
import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(value = "streaming.events", noClassnameStored = true)
@Indexes(
		@Index(fields = { @Field("replayId"), @Field("organizationId"), @Field("source") }, options = @IndexOptions(unique = true))
)
public class StreamingEvent implements Serializable {
	private static final long serialVersionUID = -226649098013040674L;
	private @Id ObjectId id;
	private Date eventDate;
	private ObjectId organizationId;
	private Long replayId;
	private String type;
	private String source;
	private Payload payload;
}