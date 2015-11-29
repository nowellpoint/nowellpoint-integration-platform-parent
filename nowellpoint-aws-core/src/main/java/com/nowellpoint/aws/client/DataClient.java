package com.nowellpoint.aws.client;

import java.io.IOException;

import redis.clients.jedis.Jedis;

import com.nowellpoint.aws.tools.RedisSerializer;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.GetDocumentRequest;
import com.nowellpoint.aws.model.data.GetDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;
import com.nowellpoint.aws.model.idp.GetCustomDataRequest;
import com.nowellpoint.aws.model.idp.GetCustomDataResponse;
import com.nowellpoint.aws.model.idp.GetTokenRequest;
import com.nowellpoint.aws.model.idp.GetTokenResponse;
import com.nowellpoint.aws.model.idp.RevokeTokenRequest;
import com.nowellpoint.aws.model.idp.RevokeTokenResponse;
import com.nowellpoint.aws.model.data.DeleteDocumentRequest;
import com.nowellpoint.aws.model.data.DeleteDocumentResponse;

public class DataClient extends AbstractClient {
	
	private Jedis jedis;
	private String accessToken;
	
	public DataClient() {
		
		long startTime = System.currentTimeMillis();
		
		System.out.println("Authenticating...");
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(System.getenv("STORMPATH_USERNAME"))
				.withPassword(System.getenv("STORMPATH_PASSWORD"));
		
		//GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(System.getenv("NOWELLPOINT_API_KEY_ID"))
		//		.withPassword(System.getenv("NOWELLPOINT_API_KEY_SECRET"));
		
		try {
			GetTokenResponse tokenResponse = invoke("IdentityProviderUsernamePasswordAuthentication", tokenRequest, GetTokenResponse.class);
			
			//GetTokenResponse tokenResponse = invoke("IdentityProviderClientCredentialsAuthentication", tokenRequest, GetTokenResponse.class);
			
			accessToken = tokenResponse.getToken().getAccessToken();
			
			System.out.println("Authenticating...success: " + tokenResponse.getToken().getStormpathAccessTokenHref());
			System.out.println("Setting up session...");
			
			GetCustomDataRequest customDataRequest = new GetCustomDataRequest().withAccessToken(accessToken);
			
			GetCustomDataResponse customDataResponse = invoke("IdentityProviderGetCustomData", customDataRequest, GetCustomDataResponse.class);
			
			System.out.println(customDataResponse.getCustomData().getMongodbConnectUri());
			
			long cacheStartTime = System.currentTimeMillis();
			
			jedis = new Jedis("pub-redis-10497.us-east-1-2.3.ec2.garantiadata.com", 10497);
			jedis.auth(System.getenv("REDIS_PASSWORD"));
			
			System.out.println("Authenticate Redis: " + (System.currentTimeMillis() - cacheStartTime));
			
			jedis.set(accessToken.getBytes(), customDataResponse.getCustomData().getMongodbConnectUri().getBytes());
			
			System.out.println("Setting up session...complete");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Authenticate: " + (System.currentTimeMillis() - startTime));
	}

	public CreateDocumentResponse create(CreateDocumentRequest createDocumentRequest) throws IOException {
		return invoke("CreateDocument", createDocumentRequest, CreateDocumentResponse.class, accessToken);
	}
	
	public UpdateDocumentResponse update(UpdateDocumentRequest updateDocumentRequest) throws IOException {
		return invoke("UpdateDocument", updateDocumentRequest, UpdateDocumentResponse.class);
	}
	
	public GetDocumentResponse get(GetDocumentRequest getDocumentRequest) throws IOException {
		return invoke("GetDocument", getDocumentRequest, GetDocumentResponse.class);
	}
	
	public DeleteDocumentResponse delete(DeleteDocumentRequest deleteDocumentRequest) throws IOException {
		return invoke("DeleteDocument", deleteDocumentRequest, DeleteDocumentResponse.class);
	}
	
	@Override
	public void close() {
		long startTime = System.currentTimeMillis();
		
		RevokeTokenRequest revokeTokenRequest = new RevokeTokenRequest().withAccessToken(accessToken);
		try {
			invoke("RevokeToken", revokeTokenRequest, RevokeTokenResponse.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		jedis.del(accessToken.getBytes());
		jedis.close();
		
		System.out.println("Close Time: " + (System.currentTimeMillis() - startTime));
	}
}