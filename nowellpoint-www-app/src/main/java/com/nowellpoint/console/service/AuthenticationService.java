package com.nowellpoint.console.service;

import java.io.UnsupportedEncodingException;

import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.oauth.OAuthClient;
import com.nowellpoint.oauth.model.AuthenticationRequest;
import com.nowellpoint.oauth.model.OktaOAuthProvider;
import com.nowellpoint.oauth.model.TokenResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationService extends AbstractService {
	
	public AuthenticationService() {
		
	}
	
	public void revoke(String accessToken) {		
		OAuthClient client = OAuthClient.builder()
				.provider(OktaOAuthProvider.builder().build())
				.build();
		
		client.revoke(accessToken);
	}

	public Token authenticate(String username, String password) throws UnsupportedEncodingException {	
		AuthenticationRequest request = AuthenticationRequest.builder()
				.password(password)
				.username(username)
				.build();
		
		OAuthClient client = OAuthClient.builder()
				.provider(OktaOAuthProvider.builder().build())
				.build();
		
		TokenResponse response = client.authenticate(request);
		
		Jws<Claims> claims = client.getClaims(response.getAccessToken());
		
		IdentityService identityService = new IdentityService();
		
		Identity identity = identityService.getBySubject(claims.getBody().getSubject());
		
		String jws = Jwts.builder()
				.setHeaderParam("kid", claims.getHeader().getKeyId())
				.setId(claims.getBody().getId())
				.setIssuer(claims.getBody().getIssuer())
				.setAudience(identity.getOrganization().getId().toString())
				.setSubject(identity.getUserId())
				.setExpiration(claims.getBody().getExpiration())
				.setIssuedAt(claims.getBody().getIssuedAt())
				.claim("scope", claims.getBody().get("groups"))
				.signWith(SignatureAlgorithm.HS256, "secret".getBytes("UTF-8"))
				.compact();

		Token token = Token.builder()
				.environmentUrl(claims.getBody().getAudience())
				.id(identity.getId().toString())
				.accessToken(jws)
				.expiresIn(response.getExpiresIn())
				.refreshToken(response.getRefreshToken())
				.tokenType(response.getTokenType())
				.build();
		
		return token;
	}
}