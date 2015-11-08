package com.nowellpoint.aws;

import com.nowellpoint.aws.idp.TestIdp;
import com.nowellpoint.aws.sforce.TestSforce;

public class TestHelper {
	
	public static void main(String[] args) {
		TestIdp testIdp = new TestIdp();
		testIdp.main();
		
		TestSforce testSforce = new TestSforce();
		testSforce.main();
	}
}