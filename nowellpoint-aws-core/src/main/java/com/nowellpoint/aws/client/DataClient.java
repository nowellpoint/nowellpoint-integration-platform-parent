package com.nowellpoint.aws.client;

import static com.nowellpoint.aws.tools.TokenParser.parseToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.io.IOException;

import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.GetDocumentRequest;
import com.nowellpoint.aws.model.data.GetDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;
import com.nowellpoint.aws.model.idp.GetTokenRequest;
import com.nowellpoint.aws.model.idp.GetTokenResponse;

public class DataClient extends AbstractClient {
	
	private String accessToken;
	
	public DataClient() {
		long startTime = System.currentTimeMillis();
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(System.getenv("STORMPATH_USERNAME"))
				.withPassword(System.getenv("STORMPATH_PASSWORD"));
		
		GetTokenResponse tokenResponse;
		try {
			tokenResponse = invoke("IDP_UsernamePasswordAuthentication", tokenRequest, GetTokenResponse.class);
			accessToken = tokenResponse.getToken().getAccessToken();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Authenticate: " + (System.currentTimeMillis() - startTime));
	}
	
	public DataClient(String accessToken) {
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
	
	public GetDocumentResponse get(GetDocumentRequest documentRequest) throws IOException {
		return invoke("GetDocument", documentRequest, GetDocumentResponse.class);
	}
}