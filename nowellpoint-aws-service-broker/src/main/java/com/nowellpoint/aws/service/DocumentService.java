package com.nowellpoint.aws.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.io.IOException;
import java.util.Base64;

import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;

public class DocumentService extends AbstractService {
	
	private String accessToken;
	
	public DocumentService(String accessToken) {
		this.accessToken = accessToken;
	}

	public CreateDocumentResponse create(CreateDocumentRequest documentRequest) throws IOException {
		Jws<Claims> claims = parseToken(accessToken);
		System.out.println(claims.getBody().getSubject());
		return invoke("CreateDocument", documentRequest, CreateDocumentResponse.class);
	}
	
	public UpdateDocumentResponse update(UpdateDocumentRequest documentRequest) throws IOException {
		return invoke("UpdateDocument", documentRequest, UpdateDocumentResponse.class);
	}
	
	private Jws<Claims> parseToken(String token) {
		return Jwts.parser()
				.setSigningKey(Base64.getUrlEncoder().encodeToString(System.getenv("STORMPATH_API_KEY_SECRET").getBytes()))
				.parseClaimsJws(token);
	}
}