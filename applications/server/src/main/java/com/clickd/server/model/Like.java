package com.clickd.server.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Like extends Resource {

	@Id
	protected String id;
	protected String ref;
	
	// TODO: Ralph - extract this to an abstract FB resource
	protected String fbId;
	
	protected String name;
	private String category;

	public Like() {
		super();
		createRef();
	}

	public Like(String fbId, String name) {
		super();
		this.fbId = fbId;
		this.name = name;
	}

	private void createRef() {
		UUID uuid = UUID.randomUUID();
		String ref = "/likes/" + ((Long) Math.abs(uuid.getMostSignificantBits())).toString();
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

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory()
	{
		return this.category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
}
