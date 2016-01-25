package com.nowellpoint.aws.tools;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.util.Base64;

public class TokenParser {
	
	public static Jws<Claims> parseToken(String key, String token) {
		return Jwts.parser()
				.setSigningKey(Base64.getUrlEncoder().encodeToString(key.getBytes()))
				.parseClaimsJws(token);
	}
	
	public static String getSubject(String key, String token) {
		return parseToken(key, token).getBody().getSubject();
	}
	
	public static String getSubjectId(String key, String token) {
		String subject = getSubject(key, token);
		subject = subject.substring(subject.lastIndexOf("/") + 1);
		return subject;
	}
}