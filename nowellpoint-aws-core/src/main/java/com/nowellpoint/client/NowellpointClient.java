package com.nowellpoint.client;

import com.nowellpoint.client.auth.BasicCredentials;
import com.nowellpoint.client.auth.Credentials;

public class NowellpointClient {
	
	public NowellpointClient(Credentials credentials) {
		if (credentials instanceof BasicCredentials) {
			System.out.println("true");
		}
		
	}

}