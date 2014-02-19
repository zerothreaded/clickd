package com.clickd.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Answer extends Resource {

	@Id
	protected String id;
	protected String ref;

	protected String answer;
		
	// protected List<Link> sessionLinks = new ArrayList<Link>();

	public Answer()
	{
		super();
		createRef();
	}
	
	private void createRef()
	{
		UUID uuid = UUID.randomUUID();
		String ref = "/users/" + ((Long)Math.abs(uuid.getMostSignificantBits())).toString();
		this.ref = ref;
	}
	
	public Answer(String answer) {
		super();

		createRef();
		this.answer = answer;
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
	
	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

}
