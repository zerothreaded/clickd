package com.clickd.server.model;

import org.springframework.data.annotation.Id;

public class Link {

	@Id
	protected Long id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	protected String href;
	
	protected String rel;

	public Link() {
		super();
	}

	public Link(String href, String rel) {
		this.id = 31415L;
		this.href = href;
		this.rel = rel;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

}
