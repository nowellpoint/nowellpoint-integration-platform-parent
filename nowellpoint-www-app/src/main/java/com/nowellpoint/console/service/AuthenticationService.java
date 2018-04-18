package com.nowellpoint.console.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.InternalServerErrorException;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.entity.IdentityDAO;
import com.nowellpoint.console.entity.IdentityDocument;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.oauth.OAuthClient;
import com.nowellpoint.oauth.model.AuthenticationRequest;
import com.nowellpoint.oauth.model.OktaOAuthProvider;
import com.nowellpoint.oauth.model.TokenResponse;
import com.nowellpoint.www.app.view.AuthenticationController;

import freemarker.log.Logger;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import spark.Request;
import spark.Response;

public class AuthenticationService {
	
	private static final Logger LOGGER = Logger.getLogger(AuthenticationController.class.getName());
	private static final String AUTH_TOKEN = "com.nowellpoint.auth.token";
	private static final String REDIRECT_URI = "redirect_uri";
	
	private IdentityDAO identityDAO;
	
	public AuthenticationService(Datastore datastore) {
		identityDAO = new IdentityDAO(IdentityDocument.class, datastore);
	}
	
	public String authentication(Request request, Response response) {
		String username = request.queryParams("username");
		String password = request.queryParams("password");
		
		request.session().invalidate();
		
		Token token = null;
		try {
			token = authenticate(username, password);
		} catch (UnsupportedEncodingException e) {
			throw new InternalServerErrorException(e);
		}
		
		Long expiresIn = token.getExpiresIn();

		try {
			System.out.println(new ObjectMapper().writeValueAsString(token));
			response.cookie(AUTH_TOKEN, new ObjectMapper().writeValueAsString(token), expiresIn.intValue(), true);
		} catch (IOException e) {
			throw new InternalServerErrorException(e);
		}
		
		return "";
		
	}
	
	public void revoke(String accessToken) {		
		OAuthClient client = OAuthClient.builder()
				.provider(OktaOAuthProvider.builder().build())
				.build();
		
		client.revoke(accessToken);
	}

	private Token authenticate(String username, String password) throws UnsupportedEncodingException {	
		AuthenticationRequest request = AuthenticationRequest.builder()
				.password(password)
				.username(username)
				.build();
		
		OAuthClient client = OAuthClient.builder()
				.provider(OktaOAuthProvider.builder().build())
				.build();
		
		TokenResponse response = client.authenticate(request);
		
		Jws<Claims> claims = client.getClaims(response.getAccessToken());
		
		IdentityDocument document = queryIdentity(claims.getBody().getSubject());
		
		String jws = Jwts.builder()
				.setHeaderParam("kid", claims.getHeader().getKeyId())
				.setId(claims.getBody().getId())
				.setIssuer(claims.getBody().getIssuer())
				.setAudience(document.getOrganizationId())
				.setSubject(document.getUserId())
				.setExpiration(claims.getBody().getExpiration())
				.setIssuedAt(claims.getBody().getIssuedAt())
				.claim("scope", claims.getBody().get("groups"))
				.signWith(SignatureAlgorithm.HS256, "secret".getBytes("UTF-8"))
				.compact();

		Token token = Token.builder()
				.environmentUrl(claims.getBody().getAudience())
				.id(document.getId().toString())
				.accessToken(jws)
				.expiresIn(response.getExpiresIn())
				.refreshToken(response.getRefreshToken())
				.tokenType(response.getTokenType())
				.build();
		
		return token;
	}
	
	private IdentityDocument queryIdentity(String subject) {
		Query<IdentityDocument> query = identityDAO.getDatastore()
				.createQuery(IdentityDocument.class)
				.field("subject")
				.equal(subject);
		
		IdentityDocument document = identityDAO.findOne(query);
		
		return document;
	}
}