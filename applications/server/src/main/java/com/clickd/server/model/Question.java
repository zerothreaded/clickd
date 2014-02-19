package com.clickd.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Question extends Resource {

	@Id
	protected String id;
	protected String ref;

	protected String question;
	protected String source;
		
	// protected List<Link> sessionLinks = new ArrayList<Link>();

	public Question()
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
	
	public Question(String question, String source) {
		super();

		createRef();
		this.question = question;
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
	
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

}
