package com.nowellpoint.aws.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.api.codec.BackupHistoryCodec;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.AbstractDocument;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="backup.history", codec=BackupHistoryCodec.class)
public class BackupHistory extends AbstractDocument implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 4880299116047933778L;
	
	public BackupHistory() {
		
	}

}