package com.clickd.server.model;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Connection extends Resource {

	@Id
	protected String id;
	protected String ref;

	protected Date createdOn;
	protected Date lastModified;
	
	protected String status;
	
	public Connection() {
		super();
	}
	
	public Connection(Date createdOn, Date lastModified, String status) {
		super();
		createRef();
		this.createdOn = createdOn;
		this.lastModified = lastModified;
		this.status = status;
	}
	
	private void createRef() {
		UUID uuid = UUID.randomUUID();
		String ref = "/connections/" + ((Long) Math.abs(uuid.getMostSignificantBits())).toString();
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
}
