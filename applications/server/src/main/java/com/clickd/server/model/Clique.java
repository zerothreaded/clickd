package com.clickd.server.model;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Clique extends Resource {

	@Id
	protected String id;
	protected String ref;

	protected Date createdOn;
	protected Date lastModified;
	

	protected String source;
	protected String name;
	
	public Clique() {
		super();

	}
	
	public Clique(User user, Date createdOn, Date lastModified, String source, String name) {
		super();
		createRef(user.getRef());
		this.createdOn = createdOn;
		this.lastModified = lastModified;
		this.source = source;
		this.name=name;
	}
	
	private void createRef(String userRef) {
		UUID uuid = UUID.randomUUID();
		String ref = userRef + "/cliques/" + ((Long) Math.abs(uuid.getMostSignificantBits())).toString();
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

	public String getSource() {
		return source;
	}

	public void setSource(String status) {
		this.source = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
