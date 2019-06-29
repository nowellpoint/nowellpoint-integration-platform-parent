package com.nowellpoint.client.sforce.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenRequest {
	private String clientId;
	private String clientSecret;
	private String refreshToken;
}