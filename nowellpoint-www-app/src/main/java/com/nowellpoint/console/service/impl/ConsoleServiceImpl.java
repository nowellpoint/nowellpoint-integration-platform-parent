package com.nowellpoint.console.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;

import com.nowellpoint.console.exception.ConsoleException;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.IdentityRequest;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.OrganizationRequest;
import com.nowellpoint.console.model.SignUpRequest;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.service.ConsoleService;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.oauth.OAuthClient;
import com.nowellpoint.oauth.model.AuthenticationRequest;
import com.nowellpoint.oauth.model.OktaOAuthProvider;
import com.nowellpoint.oauth.model.TokenResponse;
import com.nowellpoint.util.SecretsManager;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class ConsoleServiceImpl implements ConsoleService {

	@Override
	public Identity signUp(SignUpRequest request) {
		
		String uuid = UUID.randomUUID().toString();
		
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
    			.password(generatePassword())
    			.timeZone(request.getTimeZone())
    			.build();
		
		Identity identity = ServiceClient.getInstance()
    			.identity()
    			.create(identityRequest);
		
		return identity;
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
	
	//Password requirements: at least 8 characters, a lower case letter, an upper case letter, a number, a symbol.)
	private String generatePassword() {
	
	    String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
	    String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
	    String numbers = RandomStringUtils.randomNumeric(2);
	    String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
	    String totalChars = RandomStringUtils.randomAlphanumeric(2);
	    
	    String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
	    		.concat(numbers)
	    		.concat(specialChar)
	    		.concat(totalChars);
	    
	    List<Character> pwdChars = combinedChars.chars()
	    		.mapToObj(c -> (char) c)
	    		.collect(Collectors.toList());
	    
	    Collections.shuffle(pwdChars);
	    
	    String password = pwdChars.stream()
	    		.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
	    		.toString();
	    
	    return password;
	}
}