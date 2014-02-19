package com.clickd.server.model;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Session extends Resource {

	@Id
	protected String id;
	protected String ref;
	
	protected Date createdOn;
	protected Date lastModified;
	protected Long numberOfLogins;
	protected Boolean isLoggedIn;

	public Session()
	{
		super();
	}
	
	public Session(User user, Date createdOn, Date lastModified, Long numberOfLogins, Boolean isLoggedIn) {
		super();
		
		createRef(user.getRef());
		
		this.createdOn = createdOn;
		this.lastModified = lastModified;
		this.numberOfLogins = numberOfLogins;
		this.isLoggedIn = isLoggedIn;
	}

	private void createRef(String userRef)
	{
		UUID uuid = UUID.randomUUID();
		String ref = userRef + "/sessions/" + ((Long)Math.abs(uuid.getMostSignificantBits())).toString();
		this.ref = ref;
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
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
