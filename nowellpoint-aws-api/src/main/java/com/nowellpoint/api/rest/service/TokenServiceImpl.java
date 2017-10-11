package com.nowellpoint.api.rest.service;

import java.math.BigInteger;
import java.net.URI;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;

import com.nowellpoint.api.idp.model.Key;
import com.nowellpoint.api.idp.model.Keys;
import com.nowellpoint.api.idp.model.TokenResponse;
import com.nowellpoint.api.rest.IdentityResource;
import com.nowellpoint.api.rest.domain.Token;
import com.nowellpoint.api.rest.domain.UserProfile;
import com.nowellpoint.api.service.AuthenticationService;
import com.nowellpoint.api.service.TokenService;
import com.nowellpoint.api.service.UserProfileService;
import com.nowellpoint.util.Properties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolverAdapter;

public class TokenServiceImpl implements TokenService {
	
	private static final Logger LOGGER = Logger.getLogger(TokenServiceImpl.class);
	private static final Map<String,Key> KEY_CACHE = new ConcurrentHashMap<String,Key>();
	
	@Inject
	private AuthenticationService authenticationService;
	
	@Inject
	private UserProfileService userProfileService;
	
	@Inject
	private Event<UserProfile> loggedInEvent;
	
	@Override
	public Token createToken(TokenResponse tokenResponse) {
		Jws<Claims> jwsClaims = Jwts.parser()
				.setSigningKeyResolver(new SigningKeyResolverAdapter() {
					@SuppressWarnings("rawtypes")
					public java.security.Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
		        		Key key = getKey(jwsHeader.getKeyId());
		                try {
		                    BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.getModulus()));
		                    BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.getExponent()));
		                    return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));
		                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
		                	LOGGER.error(e);
		                    return null;
		                }
		            }})
                .parseClaimsJws(tokenResponse.getAccessToken());
		
		UserProfile userProfile = lookupUserProfile(jwsClaims.getBody().getSubject());
		
		String userId = userProfile.getId();
		String organizationId = userProfile.getOrganization().getId();
		
		URI href = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(IdentityResource.class)
				.path("{organizationId}")
				.path("{userId}")
				.build(organizationId, userId);
		
		SecretKey key = new SecretKeySpec(KEY_CACHE.get(jwsClaims.getHeader().getKeyId()).getModulus().getBytes(), "HmacSHA512");
		
		String jws = Jwts.builder()
				.setHeaderParam("kid", jwsClaims.getHeader().getKeyId())
				.setId(jwsClaims.getBody().getId())
				.setIssuer(jwsClaims.getBody().getIssuer())
				.setAudience(organizationId)
				.setSubject(userId)
				.setExpiration(jwsClaims.getBody().getExpiration())
				.setIssuedAt(jwsClaims.getBody().getIssuedAt())
				.claim("scope", jwsClaims.getBody().get("groups"))
				.signWith(SignatureAlgorithm.HS512, key)
				.compact();

		Token token = Token.builder()
				.environmentUrl(System.getProperty(Properties.API_HOSTNAME))
				.id(href.toString())
				.accessToken(jws)
				.expiresIn(tokenResponse.getExpiresIn())
				.refreshToken(tokenResponse.getRefreshToken())
				.tokenType(tokenResponse.getTokenType())
				.build();
		
		//
		// fire event for handling login functions
		//
		
		loggedInEvent.fire(userProfile);
        
        return token;
	}
	
	@Override
	public Jws<Claims> verifyToken(String accessToken) {
		return Jwts.parser()
				.setSigningKeyResolver(new SigningKeyResolverAdapter() {
		        	@SuppressWarnings("rawtypes")
					public java.security.Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
		        		Key key = getKey(jwsHeader.getKeyId());
		        		return new SecretKeySpec(key.getModulus().getBytes(), "HmacSHA512");
		            }
		        }).parseClaimsJws(accessToken);
	}
	
	private UserProfile lookupUserProfile(String userId) {
		UserProfile userProfile = userProfileService.findByReferenceId(userId);
		return userProfile;
	}
	
	private Key getKey(String keyId) {
		if (! KEY_CACHE.containsKey(keyId)) {
			addKeys();
		}
		
		Key key = KEY_CACHE.get(keyId);
		
		return key;
	}
	
	private void addKeys() {
		Keys keys = authenticationService.getKeys();
		keys.getKeys().forEach(key -> {
			KEY_CACHE.put(key.getKeyId(), key);
		});
	}
}