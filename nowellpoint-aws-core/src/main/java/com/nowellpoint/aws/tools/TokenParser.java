package com.nowellpoint.aws.tools;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.util.Base64;

import com.nowellpoint.aws.model.Configuration;

public class TokenParser {
	
	public static Jws<Claims> parseToken(String token) {
		return Jwts.parser()
				.setSigningKey(Base64.getUrlEncoder().encodeToString(Configuration.getStormpathApiKeySecret().getBytes()))
				.parseClaimsJws(token);
	}
}