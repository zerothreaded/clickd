package com.clickd.server.model;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Session {

	@Id
	protected String id;

	protected Long token;

	protected Date createdOn;
	protected Date lastModified;
	protected Long numberOfLogins;
	protected Boolean isLoggedIn;

	public Session()
	{
		super();
	}
	
	public Session(Long token, Date createdOn, Date lastModified,
			Long numberOfLogins, Boolean isLoggedIn) {
		super();
		this.token = token;
		this.createdOn = createdOn;
		this.lastModified = lastModified;
		this.numberOfLogins = numberOfLogins;
		this.isLoggedIn = isLoggedIn;
	}

	public static Long createToken()
	{
		UUID uuid = UUID.randomUUID();
		return uuid.getMostSignificantBits();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getToken() {
		return token;
	}

	public void setToken(Long token) {
		this.token = token;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Long getNumberOfLogins() {
		return numberOfLogins;
	}

	public void setNumberOfLogins(Long numberOfLogins) {
		this.numberOfLogins = numberOfLogins;
	}

	public Boolean getIsLoggedIn() {
		return isLoggedIn;
	}

	public void setIsLoggedIn(Boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

}
