package com.clickd.server.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Session {

	
	@Id
	protected String id;

	protected String token;
	
	protected Date createdOn;
	protected Date lastModified;
	protected Long numberOfLogins;
	protected Boolean isLoggedIn;
	
	
}
