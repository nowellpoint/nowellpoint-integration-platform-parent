package com.nowellpoint.listener.model;

import org.bson.types.ObjectId;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class StreamingEventReceived {
	private ObjectId id;
}