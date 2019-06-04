package com.nowellpoint.client.sforce.model.changeevent;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Event implements Serializable {

	private static final long serialVersionUID = -2457215427134346117L;
	
	@Getter @JsonProperty("replayId") private Long replayId;
}