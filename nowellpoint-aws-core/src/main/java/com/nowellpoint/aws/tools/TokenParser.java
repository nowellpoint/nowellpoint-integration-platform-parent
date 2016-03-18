package com.nowellpoint.aws.tools;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.util.Base64;

public class TokenParser {
	
	public static String parseToken(String key, String token) {
		Jws<Claims> claims = Jwts.parser()
				.setSigningKey(Base64.getUrlEncoder().encodeToString(key.getBytes()))
				.parseClaimsJws(token); 
		
		return claims.getBody().getSubject();
	}
}