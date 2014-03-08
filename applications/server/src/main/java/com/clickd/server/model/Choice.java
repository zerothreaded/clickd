package com.clickd.server.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Choice extends Resource {

	@Id
	protected String id;
	protected String ref;
	
	protected String answerText;

	public Choice() {
		super();
		createRef();
	}

	private void createRef() {
		UUID uuid = UUID.randomUUID();
		String ref = "/choices/" + ((Long) Math.abs(uuid.getMostSignificantBits())).toString();
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

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}
	
	public String getAnswerText()
	{
		return this.answerText;
	}

}
