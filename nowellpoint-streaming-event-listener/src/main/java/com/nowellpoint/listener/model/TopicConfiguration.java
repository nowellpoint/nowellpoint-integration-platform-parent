package com.nowellpoint.listener.model;

import java.util.List;

import com.amazonaws.services.s3.model.S3Object;
import com.nowellpoint.listener.util.JsonbUtil;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TopicConfiguration {
	private String organizationId;
	private String apiVersion;
	private String refreshToken;
	private List<Topic> topics;
	
	public static TopicConfiguration of(S3Object s3object) {
		return JsonbUtil.getJsonb().fromJson(s3object.getObjectContent(), TopicConfiguration.class);
	}
}