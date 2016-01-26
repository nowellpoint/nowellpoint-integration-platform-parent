package com.nowellpoint.aws.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nowellpoint.aws.model.admin.Property;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class CreateProperties {

	public CreateProperties() {
		
		DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
		
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.toLowerCase().endsWith("-configuration.properties")) {
					return true;
				} else {
					return false;
				}
			}
		};
		
		File folder = new File(System.getProperty("user.home").concat("/Dropbox/configuration"));
		
		Arrays.asList(folder.listFiles(filter)).stream().forEach(file -> {
			System.out.println(file.getName());
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(file));
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			List<Property> propertyBatch = new ArrayList<Property>();
			
			properties.keySet().stream().forEach(key -> {

				Property property = new Property();
				property.setStore(file.getName().replace("-configuration.properties", "").toUpperCase());
				property.setKey(((String) key).replaceAll("_", ".").toLowerCase());
				property.setValue(properties.getProperty((String) key));
				property.setLastModifiedBy("5hAh1uolQo18Nk4T8aVxci");
				property.setLastModifiedDate(Date.from(Instant.now()));
				
				propertyBatch.add(property);
			});
			
			mapper.batchSave(propertyBatch);
		});
	}

	public static void main(String[] args) {
		new CreateProperties();
	}
}
