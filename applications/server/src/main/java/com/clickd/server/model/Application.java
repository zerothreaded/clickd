package com.clickd.server.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Application extends Resource {

	@Id
	protected String id;
	protected String ref;
	
	// STATE
	private String name;
	private String version;
	private String baseUrl;

	public Application()
	{
		super();
	}
	
	public Application(String name, String version, String baseUrl) {
		super();
		createRef();
		this.name = name;
		this.version = version;
		this.baseUrl  = baseUrl;

	}

	private void createRef()
	{
		UUID uuid = UUID.randomUUID();
		String ref = "/application/" + ((Long)Math.abs(uuid.getMostSignificantBits())).toString();
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
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

}
