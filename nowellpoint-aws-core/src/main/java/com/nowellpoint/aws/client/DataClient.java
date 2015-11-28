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
import com.nowellpoint.aws.model.idp.GetTokenRequest;
import com.nowellpoint.aws.model.idp.GetTokenResponse;
import com.nowellpoint.aws.model.idp.Token;
import com.nowellpoint.aws.model.data.DeleteDocumentRequest;
import com.nowellpoint.aws.model.data.DeleteDocumentResponse;

public class DataClient extends AbstractClient {
	
	private Jedis jedis;
	private String accessToken;
	
	public DataClient() {
		
		long startTime = System.currentTimeMillis();
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(System.getenv("STORMPATH_USERNAME"))
				.withPassword(System.getenv("STORMPATH_PASSWORD"));
		
		GetTokenResponse tokenResponse;
		try {
			tokenResponse = invoke("IDP_UsernamePasswordAuthentication", tokenRequest, GetTokenResponse.class);
			accessToken = tokenResponse.getToken().getAccessToken();
			
			long cacheStartTime = System.currentTimeMillis();
			
			jedis = new Jedis("pub-redis-10497.us-east-1-2.3.ec2.garantiadata.com", 10497);
			jedis.auth(System.getenv("REDIS_PASSWORD"));
			
			System.out.println("Authenticate Redis: " + (System.currentTimeMillis() - cacheStartTime));
			
			jedis.set(accessToken.getBytes(), RedisSerializer.serialize(tokenResponse.getToken()));
			
			System.out.println("set object: " + (System.currentTimeMillis() - cacheStartTime));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Authenticate: " + (System.currentTimeMillis() - startTime));
	}
	
	public DataClient(String accessToken) {
		this.accessToken = accessToken;
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
	
	public void close() {
		long cacheStartTime = System.currentTimeMillis();
		Token token = (Token) RedisSerializer.deserialize(jedis.get(accessToken.getBytes()));
		System.out.println("Authenticate: " + (System.currentTimeMillis() - cacheStartTime));
		System.out.println(token.toString());
		jedis.close();
	}
}