package com.nowellpoint.api.test;

import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.impl.base.importer.zip.ZipImporterImpl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.UnknownExtensionTypeException;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Test;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

import com.nowellpoint.api.Main;

public class TestOpenZip {
	
	@Test
	public void testOpenZip() {
		
		
		try {
			
			String[] args = {"-Dswarm.project.stage=development"};
			
			Main.main(args);

					
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownExtensionTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
