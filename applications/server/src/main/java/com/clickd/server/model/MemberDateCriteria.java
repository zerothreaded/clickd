package com.clickd.server.model;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;

public class MemberDateCriteria extends Resource {


	@Id
	protected String id;
	protected String ref;
	
	protected List<MemberDateCriterion> criteria;
	
	public MemberDateCriteria() {	
		super();
		createRef();
	}

	private void createRef() {
		UUID uuid = UUID.randomUUID();
		String ref = "/datecriteria/" + ((Long) Math.abs(uuid.getMostSignificantBits())).toString();
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
}
