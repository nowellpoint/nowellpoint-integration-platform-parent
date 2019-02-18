package com.nowellpoint.listener.model;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class S3Event {
	private @JsonbProperty(value="Records") List<Record> records;
}