package com.nowellpoint.console.feed;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.model.FeedItem;
import com.nowellpoint.console.service.ServiceClient;

public class TestFeed {
	
	private static final String ORGANIZATION_ID = "5bac3c0e0626b951816064f5";
	
	@Test
	public void testFeed() {
		List<FeedItem> list = ServiceClient.getInstance()
				.eventStream()
				.getStreamingEventsFeed(ORGANIZATION_ID);
		
		try {
			System.out.println(new ObjectMapper().writeValueAsString(list));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}