package com.nowellpoint.console.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.validation.ValidationException;

import com.nowellpoint.console.exception.ConsoleException;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.IdentityRequest;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.OrganizationRequest;
import com.nowellpoint.console.model.SignUpRequest;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.SecretsManager;
import com.nowellpoint.oauth.OAuthClient;
import com.nowellpoint.oauth.model.AuthenticationRequest;
import com.nowellpoint.oauth.model.OktaOAuthProvider;
import com.nowellpoint.oauth.model.TokenResponse;
import com.nowellpoint.console.service.ConsoleService;
import com.okta.sdk.resource.ResourceException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class ConsoleServiceImpl implements ConsoleService {

	@Override
	public Identity signUp(SignUpRequest request) {
		
		String uuid = UUID.randomUUID().toString();
		
		try {
    		
    		OrganizationRequest organizationRequest = OrganizationRequest.builder()
    				.domain(uuid)
    				.email(request.getEmail())
    				.firstName(request.getFirstName())
    				.lastName(request.getLastName())
    				.name(uuid)
    				.planId(request.getPlanId())
    				.build();
    		
    		Organization organization = ServiceClient.getInstance()
    				.organization()
    				.create(organizationRequest);

    		IdentityRequest identityRequest = IdentityRequest.builder()
        			.email(request.getEmail())
        			.firstName(request.getFirstName())
        			.lastName(request.getLastName())
        			.locale(request.getLocale())
        			.organizationId(organization.getId())
        			.password(UUID.randomUUID().toString())
        			.timeZone(request.getTimeZone())
        			.build();
    		
    		Identity identity = ServiceClient.getInstance()
        			.identity()
        			.create(identityRequest);
			
			return identity;
			
    	} catch (ResourceException e) {
    		e.printStackTrace();
    		throw new ConsoleException(e.getError().getMessage());
    	} catch (ValidationException e) {
    		throw new ConsoleException(e.getMessage());
    	}
	}
	
	@Override
	public void revoke(String accessToken) {		
		OAuthClient client = OAuthClient.builder()
				.provider(OktaOAuthProvider.builder()
						.authorizationServer(SecretsManager.getOktaAuthorizationServer())
						.clientId(SecretsManager.getOktaClientId())
						.clientSecret(SecretsManager.getOktaClientSecret())
						.build())
				.build();
		
		client.revoke(accessToken);
	}

	@Override
	public Token authenticate(String username, char[] password) {	
		AuthenticationRequest request = AuthenticationRequest.builder()
				.password(new String(password))
				.username(username)
				.build();
		
		OAuthClient client = OAuthClient.builder()
				.provider(OktaOAuthProvider.builder()
						.authorizationServer(SecretsManager.getOktaAuthorizationServer())
						.clientId(SecretsManager.getOktaClientId())
						.clientSecret(SecretsManager.getOktaClientSecret())
						.build())
				.build();
		
		TokenResponse response = client.authenticate(request);
		
		Jws<Claims> claims = client.getClaims(response.getAccessToken());
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.getBySubject(claims.getBody().getSubject());
		
		try {
			
			String jws = Jwts.builder()
					.setHeaderParam("kid", claims.getHeader().getKeyId())
					.setId(claims.getBody().getId())
					.setIssuer(claims.getBody().getIssuer())
					.setAudience(identity.getOrganization().getId())
					.setSubject(identity.getId())
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
			
		} catch (UnsupportedEncodingException e) {
			throw new ConsoleException(e.getMessage());
		}
	}
}